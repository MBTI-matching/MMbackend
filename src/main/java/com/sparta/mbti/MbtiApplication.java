package com.sparta.mbti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MbtiApplication {

//    static {
//        System.setProperty("jasypt.encryptor.password", "5678");
//    }

    public static void main(String[] args) {
        SpringApplication.run(MbtiApplication.class, args);
    }

}
