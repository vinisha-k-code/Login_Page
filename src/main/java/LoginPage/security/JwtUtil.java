package LoginPage.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;

@Component
public class JwtUtil {
  @Value("${app.jwtSecret}")
  private String jwtSecret;
  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;
 
  public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
      .setSubject(userDetails.getUsername())
      .claim("roles", userDetails.getAuthorities().stream().map(a->a.getAuthority()).collect(Collectors.toList()))
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
      .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
      .compact();
  }
 
  public String getUsernameFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8)).build()
      .parseClaimsJws(token).getBody().getSubject();
  }
 
  public boolean validateToken(String token) {
    try { Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8)).build().parseClaimsJws(token); return true; }
    catch (JwtException ex) { return false; }
  }
}
