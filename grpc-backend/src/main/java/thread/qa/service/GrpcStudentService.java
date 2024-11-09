package thread.qa.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import thread.qa.data.StudentModel;
import thread.qa.data.repository.StudentsRepository;
import threadqa.qa.students.grpc.*;

import java.util.List;
//сервисный класс наследует сгенерированную реализацию rpc процедур, которые нужно будет реализовать
@GrpcService
public class GrpcStudentService extends StudentsServiceGrpc.StudentsServiceImplBase {

    //добавляем репозиторий в сервис
    @Autowired
    private StudentsRepository studentsRepository;

    @Override
    public void getAllStudents(Empty request, StreamObserver<GetAllStudentsResponse> responseObserver) {
        GetAllStudentsResponse response = GetAllStudentsResponse.newBuilder()
                .addAllStudents(studentsRepository.getAllStudents().stream()
                        .map(x -> threadqa.qa.students.grpc.Student.newBuilder()
                                .setId(x.getId())
                                .setAge(x.getAge())
                                .setName(x.getName())
                                .setHasJob(x.isHasJob())
                                .addAllSubjects(x.getSubjects())
                                .build())
                        .toList()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addStudent(AddStudentRequest request, StreamObserver<AddStudentResponse> responseObserver) {
        if (request.getAge() <= 17 || request.getAge() >= 23) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Student age must be between 17 and 23")
                    .asRuntimeException());
            return;
        }

        if (request.getYear() < 1 || request.getYear() >= 5) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Student year of education must be between 1 and 5")
                    .asRuntimeException());
            return;
        }


        StudentModel studentModel = StudentModel.builder()
                .age(request.getAge())
                .name(request.getName())
                .hasJob(request.getHasJob())
                .educationYear(request.getYear())
                .subjects(studentsRepository.getSubjectsByYear(request.getYear()))
                .build();

        var createdStudent = studentsRepository.save(studentModel);

        AddStudentResponse response = AddStudentResponse
                .newBuilder()
                .setStudentId(createdStudent.getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getStudent(GetStudentRequest request, StreamObserver<GetStudentResponse> responseObserver) {
        var student = studentsRepository.find(request.getStudentId());
        if (student.isPresent()) {
            StudentModel model = student.get();
            GetStudentResponse response = GetStudentResponse.newBuilder()
                    .setStudent(Student.newBuilder()
                            .setAge(model.getAge())
                            .setName(model.getName())
                            .setHasJob(model.isHasJob())
                            .addAllSubjects(model.getSubjects())
                            .setYear(model.getEducationYear())
                            .setId(model.getId()).build())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onError(Status.NOT_FOUND
                .withDescription("Student with id not found " + request.getStudentId())
                .asRuntimeException());
    }

    @Override
    public void getSubjects(com.google.protobuf.Empty request, StreamObserver<GetSubjectsResponse> responseObserver) {
        List<Subject> subjects = studentsRepository.getSubjects().entrySet()
                .stream()
                .flatMap(entry -> entry.getValue()
                        .stream()
                        .map(name -> GetSubjectsResponse.newBuilder()
                                .addSubjectsBuilder()
                                .setEducationYear(entry.getKey())
                                .setSubject(name).build()))
                .toList();

        responseObserver.onNext(GetSubjectsResponse.newBuilder().addAllSubjects(subjects).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getSubjectsByYear(GetSubjectsByYearRequest request, StreamObserver<GetSubjectsByYearResponse> responseObserver){
        if (request.getEducationYear() > 5){
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Education year must be between 1 and 5")
                    .asRuntimeException());
        }

        var response = GetSubjectsByYearResponse.newBuilder()
                .addAllSubject(studentsRepository.getSubjectsByYear(request.getEducationYear()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
