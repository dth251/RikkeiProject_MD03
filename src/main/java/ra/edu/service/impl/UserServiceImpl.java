package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.*;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.UserResponse;
import ra.edu.config.exception.BadRequestException;
import ra.edu.config.exception.ConflictException;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.entity.Role;
import ra.edu.entity.User;
import ra.edu.mapper.UserMapper;
import ra.edu.repository.UserRepository;
import ra.edu.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public PageResponse<UserResponse> getAllUsers(Boolean status, Role role, Pageable pageable) {
        Page<User> page = userRepository.findByIsActiveAndRole(status, role, pageable);

        List<UserResponse> userResponses = page.getContent().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(page, userResponses);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại!");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request, String currentUserUsername,
            boolean isAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (!isAdmin && !user.getUsername().equals(currentUserUsername)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật thông tin người dùng này.");
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại!");
        }

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUserRole(Long userId, UserRoleUpdateRequest request, String currentUserUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (user.getRole() == Role.ADMIN && !user.getUsername().equals(currentUserUsername)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật vai trò của quản trị viên khác!");
        }

        user.setRole(request.getRole());
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUserStatus(Long userId, UserStatusUpdateRequest request, String currentUserUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (user.getRole() == Role.ADMIN && !user.getUsername().equals(currentUserUsername)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật trạng thái của quản trị viên khác!");
        }

        user.setIsActive(request.getIsActive());
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void updatePassword(Long userId, UserPasswordUpdateRequest request, String currentUserUsername,
            boolean isAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (!isAdmin && !user.getUsername().equals(currentUserUsername)) {
            throw new AccessDeniedException("Bạn không có quyền đổi mật khẩu của người dùng này.");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId, String currentUserUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        if (user.getRole() == Role.ADMIN && !user.getUsername().equals(currentUserUsername)) {
            throw new AccessDeniedException("Bạn không có quyền xóa quản trị viên khác!");
        }
        userRepository.deleteById(userId);
    }
}
