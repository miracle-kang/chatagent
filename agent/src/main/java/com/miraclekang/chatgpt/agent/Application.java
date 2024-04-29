package com.miraclekang.chatgpt.agent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class Application implements CommandLineRunner {

    @Override
    public void run(String... args) {

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}