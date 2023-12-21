package com.core.market.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    private String responseCode;

    private String message;

    private T result;

    public Response(String message, T result) {
        this.message = message;
        this.result = result;
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }

    public static Response<Void> success() {
        return new Response<>("SUCCESS", null);
    }

    public static Response<Void> error(ErrorCode errorCode) {
        return new Response<>(errorCode.name(), errorCode.getMessage(),null);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + responseCode + "\"," +
                    "\"message\":" + "\"" +message + "\"" + "}";
        }

        return "{" +
                "\"resultCode\":" + "\"" + responseCode + "\"," +
                "\"result\":" + "\"" + result + "\"" + "}";
    }
}
