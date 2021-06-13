package com.simplilearn.cicd.project02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Project02ApplicationTests {
  
    @Autowired
    private TestRestTemplate restTemplate;
  
	@Test
	void testHealthcheck() {
        URI targetUrl = UriComponentsBuilder.fromUriString("/healthz")
            .build().toUri();
        
        String message = this.restTemplate.getForObject(targetUrl, String.class);
        
        assertEquals(message, "The test service is running.");
	}

}
