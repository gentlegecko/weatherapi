package com.vanguard.weatherapi.interceptor;

import com.vanguard.weatherapi.controller.WeatherApiController;
import com.vanguard.weatherapi.exception.TooManyRequestsException;
import com.vanguard.weatherapi.model.Weather;
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


@WebMvcTest(RateLimitInterceptor.class)
@TestPropertySource(locations= "classpath:application-test.properties")
public class RateLimitInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherApiController weatherApiController;

    @MockBean
    private RateLimiterService rateLimiterService;

    // Using test api keys defined in application-test.properties
    @Value("${openweathermap.api.keys}")
    private String[] apiKeys;

    @BeforeEach
    public void setup() {
        Weather weatherData = new Weather();
        weatherData.setCity("London");
        weatherData.setCountry("uk");
        weatherData.setDescription("Scattered Clouds");

        // Mock WeatherApiController to return valid weather data should API key be accepted
        Mockito.when(weatherApiController.getWeather(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(weatherData);
    }

    @Test
    public void testPreHandle_shouldReturn200() throws Exception {
        // Mock RateLimiterService to allow all requests
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiterService.resolveBucket(Mockito.anyString())).thenReturn(mockBucket);
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(true);

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
    public void testPreHandle_shouldReturn401() throws Exception {
        // Mock RateLimiterService to allow all requests for valid API keys
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiterService.resolveBucket(Mockito.anyString())).thenReturn(mockBucket);
        Mockito.when(mockBucket.tryConsume(1)).thenReturn(true);

        mockMvc.perform(get("/api/weather")
                        .param("city", "London")
                        .param("country", "uk")
                        .header("apiKey", "INVALIDKEY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testPreHandle_shouldReturn429() throws Exception {
        // Mock RateLimiterService to return 429 error
        Bucket mockBucket = Mockito.mock(Bucket.class);
        Mockito.when(rateLimiterService.resolveBucket(Mockito.anyString())).thenReturn(mockBucket);
        Mockito.when(mockBucket.tryConsume(1)).thenThrow(
                new TooManyRequestsException("API key usage limit exceeded")
        );

        mockMvc.perform(get("/api/weather")
                        .param("city", "London")
                        .param("country", "uk")
                        .header("apiKey", apiKeys[0]))
                .andExpect(status().isTooManyRequests());
    }

}
