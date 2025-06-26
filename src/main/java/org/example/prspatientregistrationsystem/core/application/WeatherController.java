package org.example.prspatientregistrationsystem.core.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class WeatherController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openweathermap.api.key:5bdbb4c04b3e437e24390b30ac4dc569}")
    private String apiKey;

    @GetMapping("/city")
    public ResponseEntity<Map> getWeatherByCity(@RequestParam String city) {
        String url = String.format(
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=pl&appid=%s",
            city, apiKey
        );
        
        try {
            Map response = restTemplate.getForObject(url, Map.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/coordinates")
    public ResponseEntity<Map> getWeatherByCoordinates(
            @RequestParam double lat, 
            @RequestParam double lon) {
        String url = String.format(
            "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&lang=pl&appid=%s",
            lat, lon, apiKey
        );
        
        try {
            Map response = restTemplate.getForObject(url, Map.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 