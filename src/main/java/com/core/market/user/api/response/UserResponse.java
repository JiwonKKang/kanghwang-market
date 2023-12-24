package com.core.market.user.api.response;

import com.core.market.user.domain.Member;

public record UserResponse(
        Long userId,
        String name,
        String address,
        Double temperature
) {
    public static UserResponse from(Member user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAddress().getProvince() + " " +
                        user.getAddress().getCity() + " " +
                        user.getAddress().getDistrict(),
                user.getTemperature()
        );
    }
}
