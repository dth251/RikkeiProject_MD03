package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.NotificationCreateRequest;
import ra.edu.dto.response.NotificationResponse;
import ra.edu.entity.Notification;
import ra.edu.entity.User;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.mapper.NotificationMapper;
import ra.edu.repository.NotificationRepository;
import ra.edu.repository.UserRepository;
import ra.edu.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationResponse> getMyNotifications(String username) {
        return notificationRepository.findByUserUsernameOrderByCreatedAtDesc(username).stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findByNotificationIdAndUserUsername(notificationId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo"));
        
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận thông báo"));
        
        Notification notification = notificationMapper.toEntity(request, user);
        notification = notificationRepository.save(notification);
        return notificationMapper.toResponse(notification);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Không tìm thấy thông báo");
        }
        notificationRepository.deleteById(notificationId);
    }
}
