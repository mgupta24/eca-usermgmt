package com.eca.usermgmt.context;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.eca.usermgmt.repository")
public class LoadRepositoryContext {
}
