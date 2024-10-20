package com.vanguard.weatherapi.interceptor;

import com.vanguard.weatherapi.exception.TooManyRequestsException;
import com.vanguard.weatherapi.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import io.github.bucket4j.Bucket;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    @Value("${openweathermap.api.keys}")
    private String[] apiKeys;

    private static final String API_KEY_HEADER = "apiKey";

    private final RateLimiterService rateLimiterService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String apiKey = request.getHeader(API_KEY_HEADER);

        if (!Arrays.asList(apiKeys).contains(apiKey)) {
            throw new IllegalArgumentException("Invalid API Key");
        }

        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException("API key usage limit exceeded");
        }

        return true;
    }
}
