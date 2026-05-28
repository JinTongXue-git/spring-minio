package com.springminio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.springminio.mapper")
@SpringBootApplication
public class SpringMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMinioApplication.class, args);
    }

}
