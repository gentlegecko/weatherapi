package com.vanguard.weatherapi.controller;

import com.vanguard.weatherapi.model.Weather;
import com.vanguard.weatherapi.service.WeatherApiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherApiController {
    private final WeatherApiService weatherApiService;

    public WeatherApiController(WeatherApiService weatherService) {
        this.weatherApiService = weatherService;
    }

    @GetMapping
    public Weather getWeather(@RequestParam String city, @RequestParam String country, @RequestHeader("apiKey") String apiKey) {

        return weatherApiService.getWeather(city, country, apiKey);
    }
}
