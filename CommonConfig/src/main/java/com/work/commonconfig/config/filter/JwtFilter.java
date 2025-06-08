package com.work.commonconfig.config.filter;

import com.work.commonconfig.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Date;

public class JwtFilter {

    // 原始密钥字符串
    private static final String SECRET_RAW = "PCBBoardDetection@2025-4-23@114yajyusenpai514###1919810!";

    // Base64 编码后的密钥，JJWT 需要传入解码后的字节数组
    private static final byte[] KEY = Base64.getEncoder().encode(SECRET_RAW.getBytes());

    private static final long EXPIRATION = 15 * 60 * 1000; // 15分钟

    public static String generateToken(String id, Integer role){
        return Jwts.builder()
                .setSubject(id)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
    }

    public static Claims getClaimsFromToken(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            System.err.println("Malformed JWT: " + token);
            throw new InvalidTokenException("Malformed JWT: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException("Invalid JWT: " + e.getMessage());
        }
    }

    public static String getUserIdFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

    public static Integer getUserRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", Integer.class);
    }

    private static boolean isTokenExpiration(String token){
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    // 判断token是否过期无效或为空
    public static boolean isValidToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean validateToken(HttpServletRequest request, String id){
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
            try {
                final String tokenId = getUserIdFromToken(token);
                if(tokenId.equals(id)){
                    if(isTokenExpiration(token)){
                        throw new InvalidTokenException("Token expired");
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
