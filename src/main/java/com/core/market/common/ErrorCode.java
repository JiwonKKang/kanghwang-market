package com.core.market.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not founded"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "post not founded"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "unauthorized user"),
    NO_PERMISSION_ERROR(HttpStatus.FORBIDDEN, "user have no permission"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "invalid access token"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "invalid refresh token"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Room not founded"),
    REFRESH(HttpStatus.OK,"refresh token sucess"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "there is internal server error"),
    COORDINATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "coorinate error");

    private final HttpStatus httpStatus;
    private final String message;

}
