package com.spectra.sports.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectra.sports.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtHelper {
    @Value("${spectra.secretKey:test}")
    private String secretKey;
    @Autowired
    private ObjectMapper objectMapper;

    public String createToken(UserDto user) throws JsonProcessingException{
        Date tokenCreatedDate = new Date(System.currentTimeMillis());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusDays(5L);
        Date expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create()
                .withIssuer("SpectraSports")
                .withSubject(objectMapper.writeValueAsString(user))
                .withIssuedAt(tokenCreatedDate)
                .withExpiresAt(expiredDate)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public UserDto parseToken(String jwt) throws JsonProcessingException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey))
                .withIssuer("SpectraSports")
                .build();
        DecodedJWT decodedJWT = verifier.verify(jwt);

        return this.objectMapper.readValue(decodedJWT.getSubject(), UserDto.class);
    }
}
