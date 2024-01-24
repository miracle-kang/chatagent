package com.miraclekang.chatgpt.common.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.miraclekang.chatgpt.common.access.AuthenticateToken;
import com.miraclekang.chatgpt.common.access.TokenService;
import com.miraclekang.chatgpt.common.access.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZonedDateTime;

@Component
@ConditionalOnMissingBean(TokenService.class)
public class JwtTokenService implements TokenService {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final String issuer;
    private final String key;
    private final Long expiresHours;

    public JwtTokenService(String issuer, String key, Long expiresHours) {
        this.issuer = issuer;
        this.key = key;
        this.expiresHours = expiresHours;
    }

    @Override
    public AuthenticateToken generateToken(UserPrincipal principal) {
        ZonedDateTime expiresTime = ZonedDateTime.now().plusHours(expiresHours);
        String token = JWT.create()
                .withIssuer(issuer)
                .withSubject(principal.getUserId())
                .withClaim("name", principal.getName())
                .withClaim("type", principal.getUserType())
                .withIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .withExpiresAt(Date.from(expiresTime.toInstant()))
                .sign(Algorithm.HMAC256(key));
        return new AuthenticateToken(TOKEN_PREFIX + token, expiresTime);
    }

    @Override
    public UserPrincipal decodeToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (token.startsWith(TOKEN_PREFIX)) {
            token = token.replaceFirst(TOKEN_PREFIX, "");
        }
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(key))
                .withIssuer(issuer)
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return new UserPrincipal(
                    jwt.getSubject(),
                    jwt.getClaim("name") == null ? null : jwt.getClaim("name").asString(),
                    jwt.getClaim("type") == null ? null : jwt.getClaim("type").asString());
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
