package com.example.bfhlwebhook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    private static final String FINAL_SQL_QUERY =
        "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
        "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
        "FROM PAYMENTS p " +
        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
        "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
        "AND p.AMOUNT = (SELECT MAX(AMOUNT) FROM PAYMENTS WHERE DAY(PAYMENT_TIME) <> 1);";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate rest = new RestTemplate();

        Map<String,String> body = Map.of(
            "name", "John Doe",
            "regNo", "REG12347",
            "email", "john@example.com"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,String>> req = new HttpEntity<>(body, headers);

        System.out.println("Sending generateWebhook request...");
        ResponseEntity<Map> response = rest.postForEntity(GENERATE_WEBHOOK_URL, req, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> respBody = response.getBody();
            String webhook = (String) respBody.get("webhook");
            String accessToken = (String) respBody.get("accessToken");

            System.out.println("Received webhook: " + webhook);
            System.out.println("Received accessToken: " + (accessToken != null ? "[REDACTED]" : "null"));

            Map<String,String> finalBody = Map.of("finalQuery", FINAL_SQL_QUERY);

            HttpHeaders submitHeaders = new HttpHeaders();
            submitHeaders.setContentType(MediaType.APPLICATION_JSON);
            submitHeaders.set("Authorization", accessToken);

            HttpEntity<Map<String,String>> submitReq = new HttpEntity<>(finalBody, submitHeaders);

            System.out.println("Posting final SQL query to webhook endpoint...");
            ResponseEntity<String> submitResp = rest.postForEntity(SUBMIT_WEBHOOK_URL, submitReq, String.class);

            System.out.println("Submission status: " + submitResp.getStatusCode());
            System.out.println("Submission response body: " + submitResp.getBody());
        } else {
            System.err.println("Failed to generate webhook. Status: " + response.getStatusCode());
            System.err.println("Response body: " + response.getBody());
        }

        System.exit(0);
    }
}

