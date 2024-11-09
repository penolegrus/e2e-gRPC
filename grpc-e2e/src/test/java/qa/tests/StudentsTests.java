package qa.tests;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import threadqa.qa.students.grpc.*;

import java.util.List;
import java.util.stream.Stream;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StudentsTests extends BaseTest {

    @Test
    @DisplayName("gRPC: Получение всех студентов")
    @Tag("gRPC")
    public void getAllStudentsTest() {
        //через клиент вызывает RPC процедуру getAllStudents и передаем необходимое тело
        GetAllStudentsResponse response = blockingStub.getAllStudents(Empty.newBuilder().build());
        //проверяем что количество студентов не равно 0
        Assertions.assertNotEquals(0, response.getStudentsCount());
    }


    @Test
    @DisplayName("gRPC: Получение всех предметов")
    @Tag("gRPC")
    public void getSubjectsTest() {
        //через клиент вызывает RPC процедуру getSubjects и передаем необходимое тело
        GetSubjectsResponse response = blockingStub.getSubjects(Empty.newBuilder().build());
        //проверяем что количество предметов не равно 0
        Assertions.assertNotEquals(0, response.getSubjectsCount());
    }

    @Test
    @DisplayName("gRPC: Добавление нового студента 1 курса")
    @Tag("gRPC")
    public void addStudentTest() {
        AddStudentResponse response = step("Создание студента", () ->
                //через клиент вызываем процедуру addStudent и на вход подаем нужное сообщение
                //которое можно создать с помощью встроенного билдера и заполняем необходимые поля
                blockingStub.addStudent(AddStudentRequest.newBuilder()
                        .setAge(18)
                        .setHasJob(false)
                        .setName("John")
                        .setYear(1)
                        .build())
        );

        //получаем информацию о созданном студенте по его ID
        GetStudentResponse studentResponse = step("Получение информации о студенте с ID: " + response.getStudentId(),
                () -> blockingStub
                        .getStudent(GetStudentRequest
                                .newBuilder()
                                .setStudentId(response.getStudentId())
                                .build()));

        //проверяем что созданный студент первого курса имеет соответствующие предметы
        assertEquals(List.of("Math", "Physics", "Philosophy", "History"), studentResponse.getStudent().getSubjectsList());
    }


    @Test
    @DisplayName("gRPC: Добавление нового студента 7 курса")
    @Tag("gRPC")
    public void addStudentBadEducationYearTest() {
        //пытаемся добавить студента 7 студента и фиксируем в переменную с ошибкой, так как gRPC не даст
        //выполнить процедуру из за полученной ошибки
        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class,
                () -> blockingStub.addStudent(AddStudentRequest.newBuilder()
                        .setAge(20)
                        .setHasJob(true)
                        .setName("Nick")
                        .setYear(7)
                        .build()
                ));
        assertEquals("INVALID_ARGUMENT: Student year of education must be between 1 and 5", exception.getMessage());
    }

    @Test
    @DisplayName("gRPC: Добавление нового студента не подходящего возраста")
    @Tag("gRPC")
    public void addStudentBadStudentAgeTest() {
        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class,
                () -> blockingStub.addStudent(AddStudentRequest.newBuilder()
                        .setAge(14)
                        .setHasJob(true)
                        .setName("Nick")
                        .setYear(2)
                        .build()
                ));
        assertEquals("INVALID_ARGUMENT: Student age must be between 17 and 23", exception.getMessage());
    }


    static Stream<Arguments> subjectsByYear() {
        return Stream.of(
                Arguments.of(1, List.of("Math", "Physics", "Philosophy", "History")),
                Arguments.of(2, List.of("Econometrics", "System Design", "Machine Learn", "Analytics")),
                Arguments.of(3, List.of("1C", "Game Theory", "Data Base", "Graph Theory")),
                Arguments.of(4, List.of("System Security", "Law", "Algorithms", "Software Engineering")),
                Arguments.of(5, List.of("Practise", "Java", "QA"))
        );
    }

    @MethodSource("subjectsByYear")
    @DisplayName("gRPC: Отображение предметов для каждого учебного года")
    @ParameterizedTest(name = "При получении предметов за учебный год №{0} должен возвращаться результат {1}")
    @Tag("gRPC")
    void getSubjectsByYearTest(int year, List<String> expectedSubjects) {
        GetSubjectsByYearRequest request = GetSubjectsByYearRequest
                .newBuilder()
                .setEducationYear(year)
                .build();

        GetSubjectsByYearResponse response = step("Получение предметов",
                () -> blockingStub.getSubjectsByYear(request));


        step("Проверка предметов", () ->
                assertEquals(expectedSubjects, response.getSubjectList())
        );
    }

    @Test
    @DisplayName("gRPC: Отображение предметов для несуществующего учебного года")
    @Tag("gRPC")
    public void getSubjectsByNotExistYearTest() {
        GetSubjectsByYearRequest request = GetSubjectsByYearRequest
                .newBuilder()
                .setEducationYear(10)
                .build();

        StatusRuntimeException exception = Assertions.assertThrows(StatusRuntimeException.class,
                () -> blockingStub.getSubjectsByYear(request));
        assertEquals("INVALID_ARGUMENT: Education year must be between 1 and 5", exception.getMessage());
    }
}
