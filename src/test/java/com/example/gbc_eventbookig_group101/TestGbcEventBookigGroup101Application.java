package com.example.gbc_eventbookig_group101;

import org.springframework.boot.SpringApplication;

public class TestGbcEventBookigGroup101Application {

    public static void main(String[] args) {
        SpringApplication.from(GbcEventBookigGroup101Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
