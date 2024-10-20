package com.vanguard.weatherapi.controller;

import com.vanguard.weatherapi.exception.CityNotFoundException;
import com.vanguard.weatherapi.exception.ExternalApiException;
import com.vanguard.weatherapi.model.Weather;
import com.vanguard.weatherapi.service.WeatherApiService;
import com.vanguard.weatherapi.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WeatherApiController.class)
@TestPropertySource(locations= "classpath:application-test.properties")
public class WeatherApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherApiService weatherApiService;

    @MockBean
    private RateLimiterService rateLimiterService;

    // Using test api keys defined in application-test.properties
    @Value("${openweathermap.api.keys}")
    private String[] apiKeys;

    private Weather weatherData;

    @BeforeEach
    public void setup() {
        weatherData = new Weather();
        weatherData.setCity("London");
        weatherData.setCountry("uk");
        weatherData.setDescription("Scattered Clouds");

        // Mock RateLimiterService to allow all requests
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiterService.resolveBucket(Mockito.anyString())).thenReturn(mockBucket);
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(true);
    }

    @Test
    public void testGetWeather_shouldReturn200() throws Exception {
        // Mock WeatherApiService to return valid weather data
        Mockito.when(weatherApiService.getWeather(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(weatherData);

        mockMvc.perform(get("/api/weather")
                        .param("city", "London")
                        .param("country", "uk")
                        .header("apiKey", apiKeys[0]))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("London"))
                .andExpect(jsonPath("$.country").value("uk"))
                .andExpect(jsonPath("$.description").value("Scattered Clouds"));
    }

    @Test
    public void testGetWeather_shouldReturn404() throws Exception {
        // Mock WeatherApiService to throw error
        Mockito.when(weatherApiService.getWeather(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new CityNotFoundException("City not found"));

        mockMvc.perform(get("/api/weather")
                        .param("city", "London")
                        .param("country", "uk")
                        .header("apiKey", apiKeys[0]))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("City not found"));
    }

    @Test
    public void testGetWeather_shouldReturn500() throws Exception {
        // Mock WeatherApiService to throw error
        Mockito.when(weatherApiService.getWeather(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new ExternalApiException("Error fetching weather data"));

        mockMvc.perform(get("/api/weather")
                        .param("city", "London")
                        .param("country", "uk")
                        .header("apiKey", apiKeys[0]))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error fetching weather data"));
    }
}
