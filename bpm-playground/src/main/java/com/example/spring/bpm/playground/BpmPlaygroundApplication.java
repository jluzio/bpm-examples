package com.example.spring.bpm.playground;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class BpmPlaygroundApplication {

  public static void main(String[] args) {
    SpringApplication.run(BpmPlaygroundApplication.class, args);
  }

}
