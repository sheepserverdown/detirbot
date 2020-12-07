package com.ssdown.detirbot;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;

public class Main {
	public static void main(String[] args) {
		//Spring Boot 이용시 진입
		//SpringApplication.run(MainBootstrap.class, args);

		//프레임워크 안쓰는 구동

		try {
			DetirBot.detirBot = new DetirBot();
		} catch(Exception e) {
			System.out.println(e.getMessage());

			System.exit(Constants.EXIT_CODE_NORMAL);
		}

	}
}
