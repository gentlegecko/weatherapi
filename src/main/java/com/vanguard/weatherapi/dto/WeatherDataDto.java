package com.vanguard.weatherapi.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherDataDto {
    private int id;
    private String main;
    private String description;
    private String icon;
}