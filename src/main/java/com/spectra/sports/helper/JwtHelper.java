package com.spectra.sports.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectra.sports.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {
    @Value("${spectra.secretKey:test}")
    private String secretKey;
    @Autowired
    private ObjectMapper objectMapper;

    public String createToken(UserDto user) throws JsonProcessingException {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date tokenCreatedDate = new Date(System.currentTimeMillis());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusDays(5L);
        Date expiredDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(this.secretKey);
        SecretKeySpec signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setId("tokenId").setIssuedAt(tokenCreatedDate).setSubject(this.objectMapper.writeValueAsString(user)).setIssuer("SpectraSports").setExpiration(expiredDate).signWith(signatureAlgorithm, signingKey);
        return builder.compact();
    }

    public UserDto parseToken(String jwt) throws JsonProcessingException {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(this.secretKey))
                .parseClaimsJws(jwt)
                .getBody();

        return this.objectMapper.readValue(claims.getSubject(), UserDto.class);
    }
}
