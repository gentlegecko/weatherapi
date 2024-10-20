package com.vanguard.weatherapi.controller;

import com.vanguard.weatherapi.exception.TooManyRequestsException;
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

        Optional<Weather> weatherData = weatherApiService.getWeather(city, country, apiKey);
        return weatherData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
