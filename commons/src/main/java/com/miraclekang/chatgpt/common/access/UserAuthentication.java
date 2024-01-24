package com.miraclekang.chatgpt.common.access;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserAuthentication implements Authentication {

    private final String token;
    private final UserPrincipal principal;
    private final UserDetails detail;

    private boolean authenticated;

    public UserAuthentication(String token, UserPrincipal principal, UserDetails detail) {
        this.token = token;
        this.principal = principal;
        this.detail = detail;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return detail == null
                ? List.of(new SimpleGrantedAuthority("ROLE_" + principal.getUserType()))
                : detail.getAuthorities();
    }

    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public UserDetails getDetails() {
        return this.detail;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    @Override
    public String getUserType() {
        return principal == null ? null : principal.getUserType();
    }

    @Override
    public String getUserId() {
        return principal == null ? null : principal.getUserId();
    }

    @Override
    public String getUsername() {
        return principal == null ? null : principal.getName();
    }
}
