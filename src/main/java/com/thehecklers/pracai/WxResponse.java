package com.thehecklers.pracai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WxResponse(@JsonProperty("clouds.all") int clouds,
                         double temp,
                         double feels_like,
                         int humidity,
                         double temp_min,
                         double temp_max,
                         double speed,
                         int deg) {

}
//public record WxResponse(int cloud_pct,
//                         double temp,
//                         double feels_like,
//                         int humidity,
//                         double min_temp,
//                         double max_temp,
//                         double wind_speed,
//                         int wind_degrees,
//                         long sunrise,
//                         long sunset) {
//}
