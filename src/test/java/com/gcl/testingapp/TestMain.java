package com.gcl.testingapp;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestMain {
  public static void main(String[] args) {
    SpringApplication.from(TestingAppApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}