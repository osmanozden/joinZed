package com.joinzad.interviewcase.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Component
public class ExchangeRateApiClient {

    @Value("${exchange.rate.api.url}")
    private String exchangeRateApiUrl;

    public double getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
        String url = String.format("%s/%s", exchangeRateApiUrl, fromCurrency);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode root = new ObjectMapper().readTree(response.getBody());
            return root.path("rates").path(toCurrency).asDouble();
        } else {
            throw new IOException("Failed to retrieve exchange rate");
        }
    }
}
