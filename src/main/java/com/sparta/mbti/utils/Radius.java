package com.sparta.mbti.utils;

import lombok.Getter;

@Getter
public class Radius {
    private Double latitude;
    private Double longitude;

    public Radius(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
