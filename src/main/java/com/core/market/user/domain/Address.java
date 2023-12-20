package com.core.market.user.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Embeddable
@ToString
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
