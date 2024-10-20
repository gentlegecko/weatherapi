package com.vanguard.weatherapi.repository;

import com.vanguard.weatherapi.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherApiRepository extends JpaRepository<Weather, Long> {
    Weather findByCityAndCountry(String city, String country);
}