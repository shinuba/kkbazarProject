package com.example.kkBazar.service.whatsapp;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiPdfService {

	private final String apiUrl = "http://148.251.129.118/wapp/api/send";
    private final String apiKey = "f988c8a585074c2c86914159ba7b1dc4";

    public String sendPdf(String mobileNumber, String message, String pdfUrl) {
        RestTemplate restTemplate = new RestTemplate();

        String url = apiUrl + "?apikey=" + apiKey + "&mobile=" + mobileNumber + "&msg=" + message + "&pdf=" + pdfUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return "Failed to send WhatsApp message. HTTP Status Code: " + response.getStatusCodeValue();
        }
    }
}