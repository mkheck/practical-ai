package com.thehecklers.pracai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.function.Function;

//public class WeatherService implements Function<WxRequest, WxResponse> {
public class WeatherService implements Function<WxRequest, String> {
    @Value("${weather.key:No valid key}")
    private String key;

    @Value("${weather.geo.url:No valid geospatial URL}")
    private String geoUrl;

    @Value("${weather.url:No valid URL}")
    private String url;

    private final RestClient client = RestClient.create();

//    public WxResponse apply(WxRequest request) {
    public String apply(WxRequest request) {
        var cities = client.get()
                .uri(geoUrl + "?q=" + request.location() + "&limit=1&appid=" + key)
//                .header("appid", key)
                .retrieve()
                .body(City[].class);

        var city = Objects.requireNonNull(cities)[0];
        var om = new ObjectMapper();
        var body = client.get()
                .uri(url + "?lat=" + city.lat() + "&lon=" + city.lon() + "&units=imperial&appid=" + key)
                .retrieve()
                .body(String.class);

        try {
            JsonNode jsonResponses = om.readTree(body)
                    .get("list");

            return jsonResponses.get(0).toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
