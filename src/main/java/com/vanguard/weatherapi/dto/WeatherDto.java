package com.vanguard.weatherapi.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherDto {
    private List<WeatherDataDto> weather;
}