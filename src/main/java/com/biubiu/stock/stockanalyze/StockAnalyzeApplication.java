package com.biubiu.stock.stockanalyze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockAnalyzeApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalyzeApplication.class, args);
    }

}
