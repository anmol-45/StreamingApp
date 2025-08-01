package com.stream.app.video_stream_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClient
public class VideoStreamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamServiceApplication.class, args);
	}

}
