package com.core.market.user.api.request;

import com.core.market.user.domain.Address;
import com.core.market.user.domain.Coordinate;
import com.core.market.user.domain.SearchScope;

public record MemberCreateRequest(
        Address address,
        Coordinate coordinate,
        SearchScope searchScope
) {
}
