package com.cingsoft.cloud.analyse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AnalyseApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyseApplication.class, args);
    }
}