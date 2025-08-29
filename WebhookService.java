package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${student.name}")
    private String studentName;

    @Value("${student.regno}")
    private String regNo;

    @Value("${student.email}")
    private String email;

    public void startProcess() {
        // Step 1: Generate webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", studentName);
        requestBody.put("regNo", regNo);
        requestBody.put("email", email);

        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, requestBody, Map.class);

        String webhookUrl = (String) response.getBody().get("webhook");
        String accessToken = (String) response.getBody().get("accessToken");

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // Step 2: Solve SQL question (manually based on regNo odd/even)
        // Example query (replace with real one from your question link)
        String finalQuery = "SELECT department, COUNT(*) FROM employees GROUP BY department;";

        // Step 3: Submit solution
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> answerBody = new HashMap<>();
        answerBody.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(answerBody, headers);

        ResponseEntity<String> submitResponse =
                restTemplate.postForEntity(webhookUrl, entity, String.class);

        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}

