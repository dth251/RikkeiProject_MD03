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
@Component // Bắt buộc để Spring quản lý và tiêm vào SecurityConfig
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 1. Log lại hành vi cố gắng truy cập khi chưa đăng nhập (hoặc token hết hạn/sai)
        log.warn("CẢNH BÁO BẢO MẬT: Truy cập chưa xác thực vào URL: {} - Lỗi: {}",
                request.getRequestURI(), authException.getMessage());

        // 2. Thiết lập Header
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        // 3. Xây dựng Body ĐÚNG CHUẨN ĐẶC TẢ DỰ ÁN (loại bỏ ErrorResponse class)
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "Bạn cần đăng nhập hoặc cung cấp Token hợp lệ để truy cập tài nguyên này.");
        body.put("data", null);
        body.put("errors", authException.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());

        // 4. Ghi ra JSON với ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), body);

        response.flushBuffer();
    }
}
