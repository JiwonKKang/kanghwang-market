package com.core.market.common.util;

import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.user.domain.repository.MemberRepository;
import com.core.market.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomAuthorityUtils {

    private final MemberRepository memberRepository;

    @Value("${admin.address}")
    private String adminMailAddress;

    private final List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");

    private final List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");

    private final List<GrantedAuthority> GUEST_ROLES = AuthorityUtils.createAuthorityList("ROLE_GUEST");

    private final List<String> ADMIN_ROLES_STRING = List.of("ROLE_ADMIN", "ROLE_USER");

    private final List<String> USER_ROLES_STRING = List.of("ROLE_USER");

    private final List<String> GUEST_ROLES_STRING = List.of("ROLE_GUEST");

    public List<GrantedAuthority> createAuthorities(String email) {
        if (email.equals(adminMailAddress)) {
            return ADMIN_ROLES;
        }

        return memberRepository.findByEmail(email)
                .map(member -> {
                    if (member.getRole().equals(Role.ROLE_GUEST)) {
                        return GUEST_ROLES;
                    }
                    return USER_ROLES;
                }).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    }

    public List<String> getAuthoritiesAsString(String email) {
        return createAuthorities(email).stream().map(String::valueOf).toList();
    }

    public List<GrantedAuthority> createAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE" + role))
                .collect(Collectors.toList());

    }

    public List<String> createRoles(String email) {
        if (email.equals(adminMailAddress)) {
            return ADMIN_ROLES_STRING;
        }

        return memberRepository.findByEmail(email)
                .map(member -> USER_ROLES_STRING)
                .orElse(GUEST_ROLES_STRING);
    }

}
