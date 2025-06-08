/*
package com.work.userservice.filter;

import com.work.userservice.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtFilter {

    @Value("${jwt.secret}")
    private static String secret;

    private static String KEY;

    private static final long EXPIRATION = 15 * 60 * 1000;

    @PostConstruct
    public void init(){
        KEY = secret;
    }
    public static String generateToken(String id){
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, KEY)
                .compact();
    }

    public static Claims getClaimsFromToken(String token){
        try{
            return Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody();
        }catch(MalformedJwtException e){
            System.err.println("Malformed JWT: " + token);  // 可以记录下错误的 Token
            throw new InvalidTokenException("Malformed JWT: " + e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            throw new InvalidTokenException("Invalid JWT: " + e.getMessage());
        }
    }

    public static String getUserIdFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

    private static boolean isTokenExpiration(String token){
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    public static boolean validateToken(HttpServletRequest request,String id){
        String token = request.getHeader("Authorization");
        if(token!=null&token.startsWith("Bearer ")){
            token = token.substring(7);
            System.out.println(token);
            try{
                final String tokenId = getUserIdFromToken(token);
                if(tokenId.equals(id)){
                    if(isTokenExpiration(token)){
                        throw new InvalidTokenException("Token expired");
                    }
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
*/
