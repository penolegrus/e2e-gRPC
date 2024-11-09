package thread.qa.data.repository;

import org.springframework.stereotype.Repository;
import thread.qa.data.StudentModel;

import java.util.*;

@Repository
public class StudentsRepository {
    private static List<StudentModel> studentModels = new ArrayList<>();
    private static Map<Integer, List<String>> subjectMap = new HashMap<>();
    private final Random random = new Random();

    static {
        subjectMap.put(1, List.of("Math", "Physics", "Philosophy", "History"));
        subjectMap.put(2, List.of("Econometrics", "System Design", "Machine Learn", "Analytics"));
        subjectMap.put(3, List.of("1C", "Game Theory", "Data Base", "Graph Theory"));
        subjectMap.put(4, List.of("System Security", "Law", "Algorithms", "Software Engineering"));
        subjectMap.put(5, List.of("Practise", "Java", "QA"));

        studentModels.add(StudentModel.builder().id(1).name("Oleg").age(18).educationYear(1).hasJob(false).subjects(subjectMap.get(1)).build());
        studentModels.add(StudentModel.builder().id(2).name("Mark").age(20).educationYear(3).hasJob(true).subjects(subjectMap.get(2)).build());
        studentModels.add(StudentModel.builder().id(3).name("Sveta").age(21).educationYear(4).hasJob(true).subjects(subjectMap.get(3)).build());
        studentModels.add(StudentModel.builder().id(4).name("Nastya").age(19).educationYear(2).hasJob(false).subjects(subjectMap.get(4)).build());
    }

    public List<StudentModel> getAllStudents() {
        return studentModels;
    }

    public Map<Integer, List<String>> getSubjects() {
        return subjectMap;
    }

    /**
     * Сохраняет студента в приложение
     * @param studentModel
     * @return
     */
    public StudentModel save(StudentModel studentModel) {
        //устанавливает рандомный id для студента, так как за ранее нельзя его знать
        //Maths.abs это модуль, так как random.nextInt может сгенерировать отрицательное число
        studentModel.setId(Math.abs(random.nextInt()));
        studentModels.add(studentModel);
        return studentModel;
    }

    /**
     * Пытается найти студента по id, если не найдет
     * @param id студент
     * @return
     */
    public Optional<StudentModel> find(int id) {
        return studentModels.stream().filter(x -> x.getId() == id).findFirst();
    }

    /**
     * Получает список предметов за определенный учебный год
     * @param year учебный год
     * @return
     */
    public List<String> getSubjectsByYear(int year) {
        return subjectMap.get(year);
    }
}
