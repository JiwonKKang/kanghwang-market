package com.core.market.trade.api.response;

import com.core.market.user.domain.Users;

public record UserResponse(
        String name,
        String address,
        Double temperature
) {
    public static UserResponse from(Users user) {
        return new UserResponse(
                user.getName(),
                user.getAddress(),
                user.getTemperature()
        );
    }
}
