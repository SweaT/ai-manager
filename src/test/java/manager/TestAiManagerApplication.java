package manager;

import org.springframework.boot.SpringApplication;

public class TestAiManagerApplication {

    public static void main(String[] args) {
        SpringApplication.from(AiManagerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
