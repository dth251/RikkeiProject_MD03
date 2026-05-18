package ra.edu.config.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component // Bắt buộc phải có @Component để Spring quản lý và tiêm vào SecurityConfig
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 1. Log lại hành vi truy cập trái phép
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.warn("CẢNH BÁO BẢO MẬT: User '{}' (Role: {}) cố gắng truy cập trái phép vào URL: {}",
                    auth.getName(), auth.getAuthorities(), request.getRequestURI());
        }

        // 2. Thiết lập header
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // 3. Xây dựng body ĐÚNG CHUẨN ĐẶC TẢ DỰ ÁN
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Bạn không có quyền truy cập tài nguyên này.");
        body.put("data", null);
        body.put("errors", accessDeniedException.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());

        // 4. Ghi ra JSON với ObjectMapper hỗ trợ Java 8 Time
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), body);

        response.flushBuffer();
    }
}
