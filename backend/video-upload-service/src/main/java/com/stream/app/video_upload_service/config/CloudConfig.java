package com.stream.app.video_upload_service.config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class CloudConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "drslaav7r");
        config.put("api_key", "757613851435727");
        config.put("api_secret", "ObV9WSTFV6RIi1ayCjoypeqNtz0");
        return new Cloudinary(config);
    }
}
