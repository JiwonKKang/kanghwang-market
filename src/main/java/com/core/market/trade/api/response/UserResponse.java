package com.core.market.trade.api.response;

import com.core.market.user.domain.Member;

public record UserResponse(
        String name,
        String address,
        Double temperature
) {
    public static UserResponse from(Member user) {
        return new UserResponse(
                user.getUsername(),
                user.getAddress(),
                user.getTemperature()
        );
    }
}
