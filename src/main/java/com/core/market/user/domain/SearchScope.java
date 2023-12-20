package com.core.market.user.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SearchScope {
    NARROW(35000.0), NORMAL(200000.0), WIDE(400000.0);

    private final Double distance;
}
