package com.core.market.user.domain;

import com.core.market.common.BaseTimeEntity;
import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.n52.jackson.datatype.jts.GeometryDeserializer;
import org.n52.jackson.datatype.jts.GeometrySerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE member SET removed_at = NOW() WHERE user_id = ?")
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;


    @Builder.Default
    private String username = "";

    @Builder.Default
    private String email = "";

    @Embedded
    @Builder.Default
    private Address address = new Address("","","");

    @Builder.Default
    private String profileImageUrl = "";

    @Enumerated(value = EnumType.STRING)
    private Role role = Role.ROLE_GUEST;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private SearchScope searchScope = SearchScope.NORMAL;


    @Column(columnDefinition = "GEOMETRY")
    @JsonSerialize(using = GeometrySerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @Builder.Default
    private Point point = coordinateToPoint(new Coordinate(0.0, 0.0));


    @Builder.Default
    private Double temperature = 36.5;

    public Member oauthUpdate(String email, String profileImageUrl) {
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    public Member update(Address address, SearchScope searchScope, Point point) {
        this.address = address;
        this.searchScope = searchScope;
        this.point = point;
        this.role = Role.ROLE_USER;
        return this;
    }

    private static Point coordinateToPoint(Coordinate coordinate) {
        String pointWKT = String.format("POINT(%f %f)", coordinate.lat(), coordinate.lng());
        try {
            return (Point) new WKTReader().read(pointWKT);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return removedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return removedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return removedAt == null;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return removedAt == null;
    }
}
