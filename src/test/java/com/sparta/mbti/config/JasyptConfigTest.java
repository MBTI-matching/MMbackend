package com.sparta.mbti.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

class JasyptConfigTest {
    @Test
    void jasypt(){
        String url = "secret_url";
        String username = "secret_username";
        String password = "secret_password";
        String accessKey = "secret_accessKey";
        String secretKey = "secret_secretKey";

        String encryptUrl = jasyptEncrypt(url);
        String encryptUsername = jasyptEncrypt(username);
        String encryptPassword = jasyptEncrypt(password);
        String encryptAccessKey = jasyptEncrypt(accessKey);
        String encryptSecretKey = jasyptEncrypt(secretKey);

        System.out.println("encryptUrl : " + encryptUrl);
        System.out.println("encryptUsername : " + encryptUsername);
        System.out.println("encryptPassword : " + encryptPassword);
        System.out.println("encryptAccessKey : " + encryptAccessKey);
        System.out.println("encryptSecretKey : " + encryptSecretKey);

        Assertions.assertThat(url).isEqualTo(jasyptDecryt(encryptUrl));
    }

    private String jasyptEncrypt(String input) {
        String key = "5678";
        System.out.println("환경변수 " + System.getenv("jasypt"));
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.encrypt(input);
    }

    private String jasyptDecryt(String input){
        String key = "5678";
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(key);
        return encryptor.decrypt(input);
    }
}