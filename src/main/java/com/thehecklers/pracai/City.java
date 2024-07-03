package com.thehecklers.pracai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record City(String name, String state, String country, double lat, double lon) {
}
