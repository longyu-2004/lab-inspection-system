package com.labinspection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.labinspection.mapper")
public class LabInspectionApplication {
    public static void main(String[] args) {
        SpringApplication.run(LabInspectionApplication.class, args);
        System.out.println("========================================");
        System.out.println("  高校实验室设备巡检管理系统启动成功");
        System.out.println("  接口地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
