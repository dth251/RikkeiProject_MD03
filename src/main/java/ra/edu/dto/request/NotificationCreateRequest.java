package ra.edu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ra.edu.entity.NotificationType;

@Data
public class NotificationCreateRequest {
    @NotNull(message = "ID Người dùng (người nhận) không được để trống")
    private Long userId;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String message;

    private NotificationType type;

    private String targetUrl;
}
