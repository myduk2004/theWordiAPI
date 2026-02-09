package theWordI.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JWTUtil {

    private static final SecretKey secretKey;
    private static final Long accessTokenExpiresIn;
    private static final Long refreshTokenExpiresIn;

    private JWTUtil(){};

    //static 초기화 블록
    static {
        String secretKeyString = "himynameiskimjihunmyyoutubechann";
        secretKey = new SecretKeySpec(secretKeyString.getBytes(StandardCharsets.UTF_8)
        , Jwts.SIG.HS256.key().build().getAlgorithm());
        accessTokenExpiresIn = 3600L * 1000; //1시간
        refreshTokenExpiresIn = 604800L * 1000; //7일
    }

    //JWT subject userid 파싱
    public static Long getUserId(String token)
    {
        return Long.valueOf(
                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject()
        );
    }


    //JWT 클레임 role 파싱
    public static String getRole(String token)
    {
        return Jwts.parser().verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    //JWT 유효 여부 (위조, 시간, Access/Refresh 여부)
    public static Boolean isValid(String token, Boolean isAccess)
    {
        try
        {
            Claims claims = Jwts.parser().verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);
            if (type == null) return false;

            if (isAccess && !type.equals("access")) return false;
            if (!isAccess && !type.equals("refresh")) return false;

            return true;
        }
        catch (JwtException | IllegalArgumentException e)
        {
            return false;
        }
    }
    

    // JWT(Access) 생성
    public static String createAccessToken(Long userId, String role)
    {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer("theWordI")
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", "access")
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenExpiresIn))
                .signWith(secretKey)
                .compact();
    }

    // JWT(Refresh) 생성
    public static String createRefreshToken(Long userId)
    {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .issuer("theWordI")
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(new Date(now))
                .expiration(new Date(now + refreshTokenExpiresIn))
                .signWith(secretKey)
                .compact();
    }

}
