package com.stream.app.video_upload_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableDiscoveryClient
public class VideoUploadServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoUploadServiceApplication.class, args);
	}

}
