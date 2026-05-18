package ra.edu.mapper;

import org.springframework.stereotype.Component;
import ra.edu.dto.request.NotificationCreateRequest;
import ra.edu.dto.response.NotificationResponse;
import ra.edu.entity.Notification;
import ra.edu.entity.User;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUser() != null ? notification.getUser().getUserId() : null)
                .message(notification.getMessage())
                .type(notification.getType())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public Notification toEntity(NotificationCreateRequest request, User user) {
        if (request == null) return null;
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setTargetUrl(request.getTargetUrl());
        return notification;
    }
}
