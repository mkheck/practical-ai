package com.thehecklers.pracai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WxResponse(int cloud_pct,
                         double temp,
                         double feels_like,
                         int humidity,
                         double min_temp,
                         double max_temp,
                         double wind_speed,
                         int wind_degrees,
                         long sunrise,
                         long sunset) {
}
