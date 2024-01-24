package com.miraclekang.chatgpt.identity;

import com.miraclekang.chatgpt.identity.domain.model.identity.AdministratorRootStrategy;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class Application implements CommandLineRunner {

    private final AdministratorRootStrategy administratorRootStrategy;

    private final List<String> rootPhoneNumbers;
    private final Boolean rootAutoInit;

    public Application(AdministratorRootStrategy administratorRootStrategy,
                       @Value("${auth.root.phones}") List<String> rootPhoneNumbers,
                       @Value("${auth.root.auto-init}") Boolean rootAutoInit) {
        this.administratorRootStrategy = administratorRootStrategy;
        this.rootPhoneNumbers = rootPhoneNumbers;
        this.rootAutoInit = rootAutoInit;
    }

    @Override
    public void run(String... args) {
        // Init root admin
        administratorRootStrategy.assignRootUsers(rootPhoneNumbers, BooleanUtils.isTrue(rootAutoInit));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}