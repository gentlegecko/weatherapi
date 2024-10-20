package com.vanguard.weatherapi.service;

import com.vanguard.weatherapi.dto.WeatherDto;
import com.vanguard.weatherapi.model.Weather;
import com.vanguard.weatherapi.repository.WeatherApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class WeatherApiService {
    private final WeatherApiRepository weatherApiRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openweathermap.api.url}")
    private String weatherMapApiUrl;

    public WeatherApiService(WeatherApiRepository weatherApiRepository) {
        this.weatherApiRepository = weatherApiRepository;
        this.restTemplate = new RestTemplate();;
    }

    public Optional<Weather> getWeather(String city, String country, String apiKey) {
        Optional<Weather> cachedData = Optional.ofNullable(weatherApiRepository.findByCityAndCountry(city, country));
        if (cachedData.isPresent()) {
            return cachedData;
        }

        String url = String.format("%s?q=%s,%s&appid=%s", weatherMapApiUrl, city, country, apiKey);
        System.out.println(url);
        WeatherDto weatherResponse = restTemplate.getForObject(url, WeatherDto.class); //returning null

        Weather weatherData = new Weather();
        weatherData.setCity(city);
        weatherData.setCountry(country);
        weatherData.setDescription(weatherResponse.getWeather().get(0).getDescription());

        System.out.println(weatherData);
        System.out.println(weatherResponse.getWeather().get(0).getDescription());
        weatherApiRepository.save(weatherData);
        return Optional.of(weatherData);
    }
}
