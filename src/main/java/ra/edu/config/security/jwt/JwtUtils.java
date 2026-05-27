package ra.edu.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ra.edu.config.security.principle.CustomUserDetails;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        String role = userPrincipal.getAuthorities().iterator().next().getAuthority();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken, HttpServletRequest request) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            request.setAttribute("jwt_error", "Token đã hết hạn");
            System.err.println("JWT token is expired: " + e.getMessage());
        } catch (SignatureException e) {
            request.setAttribute("jwt_error", "Chữ ký token không hợp lệ");
            System.err.println("Invalid JWT signature: " + e.getMessage());
        } catch (MalformedJwtException e) {
            request.setAttribute("jwt_error", "Token không đúng định dạng");
            System.err.println("Invalid JWT token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            request.setAttribute("jwt_error", "Token không được hỗ trợ");
            System.err.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            request.setAttribute("jwt_error", "Token không được để trống");
            System.err.println("JWT claims string is empty: " + e.getMessage());
        } catch (JwtException e) {
            request.setAttribute("jwt_error", "Token hết hạn hoặc không hợp lệ");
            System.err.println("JWT error: " + e.getMessage());
        }
        return false;
    }
}
