package com.vanguard.weatherapi.service;

import com.vanguard.weatherapi.config.WebConfig;
import com.vanguard.weatherapi.interceptor.RateLimitInterceptor;
import com.vanguard.weatherapi.model.Weather;
import com.vanguard.weatherapi.repository.WeatherApiRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;


@WebMvcTest(WeatherApiService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class WeatherApiServiceTest {

    @Autowired
    private WeatherApiService weatherApiService;

    @MockBean
    private WeatherApiRepository weatherApiRepository;

    @MockBean
    private RateLimitInterceptor rateLimitInterceptor;

    @MockBean
    private WebConfig webConfig;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${openweathermap.api.keys}")
    private String[] apiKeys;

    @Test
    public void testGetWeather_shouldGetDataFromRepo() {
        Weather weatherData = new Weather();
        weatherData.setCity("London");
        weatherData.setCountry("uk");
        weatherData.setDescription("Scattered Clouds");
        Mockito.when(weatherApiRepository.findByCityAndCountry("London", "uk")).thenReturn(weatherData);
        Weather weather1 = weatherApiService.getWeather("London", "uk", apiKeys[0]);
        Weather weather2 = weatherApiService.getWeather("London", "uk", apiKeys[0]);

        assertEquals(weather1.getId(), weather2.getId());
        assertEquals(weather1.getCity(), weather2.getCity());
        assertEquals(weather1.getCountry(), weather2.getCountry());
    }

}
