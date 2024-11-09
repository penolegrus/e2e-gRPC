package thread.qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentsApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(StudentsApplication.class);
        springApplication.run(args);
    }
}
