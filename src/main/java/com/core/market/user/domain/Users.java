package com.core.market.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String address;

    private Double temperature;

    private Users(String name, String address, Double temperature) {
        this.name = name;
        this.address = address;
        this.temperature = temperature;
    }

    public static Users of(String name, String address, Double temperature) {
        return new Users(
                name,
                address,
                temperature
        );
    }
}
