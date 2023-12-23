package com.core.market.user.domain;

import jakarta.persistence.Embeddable;
import lombok.*;


@Getter
@Embeddable
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Address {

    private String province;

    private String city;

    private String district;
}
