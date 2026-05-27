package ra.edu.service;

import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.*;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.entity.Role;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(Boolean status, Role role, Pageable pageable);
    UserResponse getUserById(Long userId);
    UserResponse createUser(UserCreateRequest request);
    UserResponse updateUser(Long userId, UserUpdateRequest request, String currentUserUsername, boolean isAdmin);
    UserResponse updateUserRole(Long userId, UserRoleUpdateRequest request, String currentUserUsername);
    UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request, String currentUserUsername);
    void updatePassword(Long userId, UserPasswordUpdateRequest request, String currentUserUsername, boolean isAdmin);
    void deleteUser(Long userId, String currentUserUsername);
}
