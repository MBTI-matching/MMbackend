package com.sparta.mbti;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableEncryptableProperties
@EnableJpaAuditing
@SpringBootApplication
public class MbtiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MbtiApplication.class, args);
    }
}
