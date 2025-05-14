package com.l1Akr.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Data
@Slf4j
public class JwtUtils {

    /**
     * Jwt 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 默认过期时间20分钟
     */
    @Value("${jwt.expiration:1200000}")
    private long expiredTime;

    /**
     * Token 刷新时间
     */
    @Value("${jwt.refreshTime:300000}")
    private long refreshTime;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * 生成标准 Jwt 令牌
     * @param subject 主题（通常是用户的唯一标识）
     * @param claims 自定义声明
     * @return
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        // 添加JWT标准声明
        JWTCreator.Builder jwtCreator = JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiredTime));

        // 添加自定义声明
        claims.forEach((key, value) -> {
            if (value instanceof String) {
                jwtCreator.withClaim(key, (String) value);
            } else if (value instanceof Integer) {
                jwtCreator.withClaim(key, (Integer) value);
            } else if (value instanceof Long) {
                jwtCreator.withClaim(key, (Long) value);
            } else if (value instanceof Double) {
                jwtCreator.withClaim(key, (Double) value);
            } else if (value instanceof Boolean) {
                jwtCreator.withClaim(key, (Boolean) value);
            } else if (value instanceof Date) {
                jwtCreator.withClaim(key, (Date) value);
            } else if (value instanceof List) {
                try {
                    jwtCreator.withClaim(key, objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize claim: {}", key, e);
                }
            }
        });

        return jwtCreator.sign(getAlgorithm());
    }

    /**
     * 从claims中解析出自定义对象列表
     */
    public <T> List<T> parseListClaim(Claim claim, Class<T> elementType) {
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, elementType);
            return objectMapper.readValue(claim.asString(), type);
        } catch (IOException e) {
            log.error("Failed to parse list claim", e);
            return List.of();
        }
    }

    /**
     * 验证并解析JWT令牌
     * @param token
     * @return
     */
    public DecodedJWT validateAndParse(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        return verifier.verify(token);
    }

    /**
     * 检测令牌是否快要过期
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public Boolean isTokenExpireSoon(String token) throws JWTVerificationException {
        Date expiration = validateAndParse(token).getExpiresAt();
        return expiration.getTime() - System.currentTimeMillis() < 300000;
    }

    /**
     * 获取Token中的负荷
     * @param token
     * @return
     * @throws JWTVerificationException
     */
    public String getTokenPayload(String token) throws JWTVerificationException {
        DecodedJWT decode = JWT.decode(token);

        System.out.println(decode.getExpiresAt());

        return "payload";
    }


}
