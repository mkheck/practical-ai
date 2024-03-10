package com.thehecklers.pracai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

public class WeatherService implements Function<WxRequest, WxResponse> {
    @Value("${weather.key:No valid key}")
    private String key;

    @Value("${weather.url:No valid URL}")
    private String url;

    @Value("${weather.host:No valid host}")
    private String host;

    private final RestClient client = RestClient.create();

    public WxResponse apply(WxRequest request) {
        return client.get()
                .uri(url + "?city=" + request.location())
                .header("X-RapidAPI-Key", key)
                .header("X-RapidAPI-Host", host)
                .retrieve()
                .body(WxResponse.class);
    }
}
