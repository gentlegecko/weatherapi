package com.vanguard.weatherapi.dto;

import java.util.List;

import lombok.Data;

@Data
public class WeatherDto {
    private List<WeatherDataDto> weather;
}