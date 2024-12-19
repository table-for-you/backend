package com.project.tableforyou;

import api.link.checker.annotation.EnableApiLinkChecker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableApiLinkChecker
public class TableforyouApplication {
	public static void main(String[] args) {
		SpringApplication.run(TableforyouApplication.class, args);
	}

}
