package com.beyond.Pocha_On.owner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class VerifyConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
