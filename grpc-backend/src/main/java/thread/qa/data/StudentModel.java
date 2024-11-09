package thread.qa.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentModel {
    private Integer id;
    private String name;
    private Integer age;
    private Integer educationYear;
    private boolean hasJob;
    private List<String> subjects;
}