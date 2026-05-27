package ra.edu.config.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.warn("CẢNH BÁO BẢO MẬT: Truy cập chưa xác thực vào URL: {} - Lỗi: {}",
                request.getRequestURI(), authException.getMessage());

        String jwtError = (String) request.getAttribute("jwt_error");
        if (jwtError == null) {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwtError = "Token hết hạn hoặc không hợp lệ";
            }
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        if (jwtError != null) {
            body.put("message", jwtError);
            body.put("errors", jwtError);
        } else {
            body.put("message", "Bạn cần đăng nhập hoặc cung cấp Token hợp lệ để truy cập tài nguyên này.");
            
            String errorMessage = "Yêu cầu xác thực không thành công.";
            if (authException instanceof org.springframework.security.authentication.InsufficientAuthenticationException) {
                errorMessage = "Yêu cầu xác thực đầy đủ để truy cập tài nguyên này.";
            } else if (authException instanceof org.springframework.security.authentication.BadCredentialsException) {
                errorMessage = "Tên đăng nhập hoặc mật khẩu không chính xác.";
            } else if (authException != null && authException.getMessage() != null) {
                errorMessage = authException.getMessage();
            }
            body.put("errors", errorMessage);
        }
        body.put("data", null);
        body.put("timestamp", LocalDateTime.now().toString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), body);

        response.flushBuffer();
    }
}
