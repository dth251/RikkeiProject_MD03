package ra.edu.service;

import ra.edu.dto.request.NotificationCreateRequest;
import ra.edu.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(String username);
    void markAsRead(Long notificationId, String username);
    NotificationResponse createNotification(NotificationCreateRequest request);
    void deleteNotification(Long notificationId);
}
