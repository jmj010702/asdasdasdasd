package com.beyond.Pocha_On.common.auth;

import com.beyond.Pocha_On.owner.domain.Owner;
import com.beyond.Pocha_On.owner.repository.OwnerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secretKey}")
    private String st_secret_key;
    @Value("${jwt.expirationAt}")
    private int expiration;
    private Key secret_key;

    @Value("${jwt.secretKeyRt}")
    private String st_secret_key_rt;
    @Value("${jwt.expirationRt}")
    private int expirationRt;
    private Key secret_key_rt;

    private final RedisTemplate<String, String> redisTemplate;
    private final OwnerRepository ownerRepository;

    @Autowired
    public JwtTokenProvider(@Qualifier("rtInventory") RedisTemplate<String, String> redisTemplate, OwnerRepository ownerRepository) {
        this.redisTemplate = redisTemplate;
        this.ownerRepository = ownerRepository;
    }

    @PostConstruct
    public void init() {
        secret_key = new SecretKeySpec(Base64.getDecoder().decode(st_secret_key), SignatureAlgorithm.HS512.getJcaName());
        secret_key_rt = new SecretKeySpec(Base64.getDecoder().decode(st_secret_key_rt), SignatureAlgorithm.HS512.getJcaName());
    }

     /* =========================
       Access Token
       ========================= */

    //    점주 email + password로 로그인 시 발급되는 accessToken
    public String createBaseAccessToken(
            Owner owner,
            TokenStage stage,
            Map<String, Object> extraClaims
    ) {
        Claims claims = Jwts.claims()
                .setSubject(owner.getOwnerEmail());

        claims.put("role", owner.getRole().name());
        claims.put("stage", stage.name());
        claims.put("ownerId", owner.getId());

        if (extraClaims != null) {
            claims.putAll(extraClaims);
        }

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L))
                .signWith(secret_key)
                .compact();
    }

    //    accessToken 키값으로 검증
    public Claims validateAccessToken(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret_key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 AccessToken");
        }
    }

    //    매장 선택 후 발급하는 토큰 (기존 토큰의 payload + storeId)
    public String createStoreAccessToken(Owner owner, Long storeId) {
        Claims claims = Jwts.claims()
                .setSubject(owner.getOwnerEmail());

        claims.put("role", "OWNER");
        claims.put("stage", "STORE");
        claims.put("ownerId", owner.getId());
        claims.put("storeId", storeId);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L))
                .signWith(secret_key)
                .compact();
    }

    //    테이블 선택 후 발급하는 토큰(storeId, tableNum, iat, exp)
    public String createTableToken(Long storeId, int tableNum) {

        Claims claims = Jwts.claims();

        claims.put("stage", "TABLE");
        claims.put("storeId", storeId);
        claims.put("tableNum", tableNum);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L))
                .signWith(secret_key)
                .compact();
    }

    /* =========================
       Refresh Token
       ========================= */

    //    점주 email + password로 로그인 시 발급되는 refreshToken
    public String createRefreshToken(Owner owner) {
        Claims claims = Jwts.claims()
                .setSubject(owner.getOwnerEmail());
        claims.put("role", owner.getRole().name());

        Date now = new Date();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L))
                .signWith(secret_key_rt)
                .compact();

//        redis에 저장, expirationRt분 동안 유효
        redisTemplate.opsForValue()
                .set(owner.getOwnerEmail(), refreshToken, expirationRt, TimeUnit.MINUTES);

        return refreshToken;
    }

    //    refreshToken 키 값으로 검증
    public Owner validateRefreshToken(String refreshToken) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secret_key_rt)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 RefreshToken");
        }

        String email = claims.getSubject();

        Owner owner = ownerRepository.findByOwnerEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Owner not found"));

//        redis에 저장된 토큰과 일치하는지 검증
        String redisRt = redisTemplate.opsForValue().get(email);
        if (redisRt == null) {
            throw new IllegalArgumentException("이미 사용된 RefreshToken");
        }

        if (!redisRt.equals(refreshToken)) {
            throw new IllegalArgumentException("RefreshToken mismatch");
        }

        return owner;
    }
}

