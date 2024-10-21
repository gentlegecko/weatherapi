package com.vanguard.weatherapi.service;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;


@WebMvcTest(RateLimiterService.class)
public class RateLimiterServiceTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @BeforeEach
    public void setup() {
        rateLimiterService = new RateLimiterService();
    }

    @Test
    public void testResolveBucket_createsNewBucket() throws Exception {
        String apiKey = "TESTKEY1";

        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        assertNotNull(bucket);
        assertEquals(5, bucket.getAvailableTokens());
    }

    @Test
    public void testResolveBucket_returnsSameBucketForSameApiKey() {
        String apiKey = "TESTKEY2";

        Bucket firstBucket = rateLimiterService.resolveBucket(apiKey);
        Bucket secondBucket = rateLimiterService.resolveBucket(apiKey);

        assertSame(firstBucket, secondBucket);
    }

    @Test
    public void testBucketConsumption_allowsValidConsumption() {
        String apiKey = "TESTKEY3";
        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        assertTrue(bucket.tryConsume(1));
        assertEquals(4, bucket.getAvailableTokens());
    }

    @Test
    public void testBucketConsumption_exceedsRateLimit() {
        String apiKey = "TESTKEY4";
        Bucket bucket = rateLimiterService.resolveBucket(apiKey);

        for (int i = 0; i < 5; i++) {
            bucket.tryConsume(1);
        }

        assertFalse(bucket.tryConsume(1));
        assertEquals(0, bucket.getAvailableTokens());
    }

}
