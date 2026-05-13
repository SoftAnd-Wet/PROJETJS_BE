package net.codejava.projetjs_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjetjsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetjsBeApplication.class, args);
    }

}
