package petTopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PetTopiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetTopiaApplication.class, args);
    }
}
