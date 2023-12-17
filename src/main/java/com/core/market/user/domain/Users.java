package com.core.market.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

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

    @Column(nullable = false, columnDefinition = "GEOMETRY")
    private Point point;

    private Double temperature = 36.5;

    private Users(String name, String address, Point point) {
        this.name = name;
        this.address = address;
        this.point = point;
    }

    public static Users of(String name, String address, Point point) {
        return new Users(
                name,
                address,
                point
        );
    }
}
