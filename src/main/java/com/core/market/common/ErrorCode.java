package com.core.market.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "post not founded"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "unauthorized user"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "invalid token"),
    COORDINATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "coorinate error");

    private final HttpStatus httpStatus;
    private final String message;

}
