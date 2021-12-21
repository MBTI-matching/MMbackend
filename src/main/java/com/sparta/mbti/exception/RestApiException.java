package com.sparta.mbti.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RestApiException {
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
