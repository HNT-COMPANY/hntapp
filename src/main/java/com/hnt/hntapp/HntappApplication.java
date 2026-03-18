package com.hnt.hntapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HntappApplication {

    public static void main(String[] args) {

        SpringApplication.run(HntappApplication.class, args);

    }
}
