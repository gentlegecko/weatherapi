package com.vanguard.weatherapi.controller;

import com.vanguard.weatherapi.model.Weather;
import com.vanguard.weatherapi.service.WeatherApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {
    private final WeatherApiService weatherApiService;

    public WeatherApiController(WeatherApiService weatherService) {
        this.weatherApiService = weatherService;
    }

    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam String city, @RequestParam String country, @RequestHeader("apiKey") String apiKey) {
        try {
            Optional<Weather> weatherData = weatherApiService.getWeather(city, country, apiKey);
            return weatherData.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(429).body(ex.getMessage());
        }
    }
}
