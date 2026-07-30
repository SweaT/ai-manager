package manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AiManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiManagerApplication.class, args);
    }

}
