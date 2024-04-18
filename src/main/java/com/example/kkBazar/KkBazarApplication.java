package com.example.kkBazar;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KkBazarApplication {

	public static void main(String[] args) {

		SpringApplication.run(KkBazarApplication.class, args);

		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			System.out.println("Localhost IP Address: " + localhost);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
