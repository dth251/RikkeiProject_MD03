package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.NotificationCreateRequest;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.NotificationResponse;
import ra.edu.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(auth.getName()), 
                "Lấy danh sách thông báo thành công"
        ));
    }

    @PutMapping("/{notification_id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("notification_id") Long notificationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        notificationService.markAsRead(notificationId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Đánh dấu đã đọc thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(
                notificationService.createNotification(request), 
                "Tạo thông báo thành công"
        ));
    }

    @DeleteMapping("/{notification_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable("notification_id") Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa thông báo thành công"));
    }
}
