package pl.plh.app.employment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.plh.app.employment.demo.DemoDataGenerator;

@SpringBootApplication
public class EmploymentApplication {
    @Autowired(required = false)
    private DemoDataGenerator gen;

    public static void main(String[] args) {
        SpringApplication.run(EmploymentApplication.class, args);
    }

    @Bean
    public CommandLineRunner demoData() {
         return args -> {
            if(gen != null) {
                gen.createAll();
            }
        };
    }
}
