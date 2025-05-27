package org.example.grainoilsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
    @MapperScan("org.example.grainoilsystem.mapper")
    public class GrainOilSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrainOilSystemApplication.class, args);
    }

}
