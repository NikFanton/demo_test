package com.huchenko.demo;

import com.huchenko.demo.domain.Image;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
//        ToDo Replace with variable
        headers.add("Authorization", "Bearer " + "5932b7b78af67ec88c53526803bbd5e0d5272109");
        HttpEntity request = new HttpEntity(headers);

        String endpoint = "http://interview.agileengine.com/images/ae8fa93f16194351ebd3";
        ResponseEntity<Image> response = restTemplate.exchange(endpoint, HttpMethod.GET, request, Image.class);
        System.out.println(response.getBody().getId());
    }


//	@PostConstruct
//	private void initialize() {
//	    ToDo Make as bean
//
//    }
}
