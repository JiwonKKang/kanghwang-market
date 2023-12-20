package com.core.market.user.domain;

import com.core.market.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.locationtech.jts.geom.Point;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@SQLDelete(sql = "UPDATE \"Member\" SET removed_at = NOW() WHERE id=?")
public class Member extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    private String email;

    private String address;

    private String profileImageUrl;

    private String password;

    @Enumerated
    private Role role;

    @Column(columnDefinition = "GEOMETRY")
    private Point point;

    @Builder.Default
    private Double temperature = 36.5;

    private Member(String name, String address, Point point) {
        this.username = name;
        this.address = address;
        this.point = point;
    }

    public static Member of(String name, String address, Point point) {
        return new Member(
                name,
                address,
                point
        );
    }

    public Member update(String email, String profileImageUrl) {
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        return this;
    }


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }

    @Override
    public String getPassword() {
        return password;
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
