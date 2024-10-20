package com.vanguard.weatherapi.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    @Value("${openweathermap.api.request_limit}")
    private int REQUEST_LIMIT;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        Bandwidth limit = Bandwidth
                .builder()
                .capacity(REQUEST_LIMIT)
                .refillIntervally(REQUEST_LIMIT, Duration.ofHours(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}