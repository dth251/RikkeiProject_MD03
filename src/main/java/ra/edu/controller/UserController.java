package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.*;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Boolean isActive = null;
        if (status != null && !status.isEmpty()) {
            if ("active".equalsIgnoreCase(status)) isActive = true;
            else if ("inactive".equalsIgnoreCase(status)) isActive = false;
        }

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(isActive, pageable), "Lấy danh sách người dùng thành công"));
    }

    @GetMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userId), "Lấy thông tin người dùng thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(userService.createUser(request), "Tạo người dùng thành công"));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(userId, request, auth.getName(), isAdmin), "Cập nhật thông tin thành công"));
    }

    @PutMapping("/{user_id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UserRoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserRole(userId, request), "Cập nhật quyền thành công"));
    }

    @PutMapping("/{user_id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateUserStatus(userId, request), "Cập nhật trạng thái thành công"));
    }

    @PutMapping("/{user_id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable("user_id") Long userId,
            @Valid @RequestBody UserPasswordUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        userService.updatePassword(userId, request, auth.getName(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success(null, "Đổi mật khẩu thành công"));
    }

    @DeleteMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa người dùng thành công"));
    }
}
