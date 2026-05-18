package ra.edu.service;

import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.*;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.UserResponse;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(Boolean status, Pageable pageable);
    UserResponse getUserById(Long userId);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long userId, UserUpdateRequest request, String currentUserUsername, boolean isAdmin);
    UserResponse updateUserRole(Long userId, UserRoleUpdateRequest request);
    UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request);
    void updatePassword(Long userId, UserPasswordUpdateRequest request, String currentUserUsername, boolean isAdmin);
    void deleteUser(Long userId);
}
