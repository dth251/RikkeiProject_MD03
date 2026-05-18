package ra.edu.service;

import ra.edu.dto.request.LoginRequest;
import ra.edu.dto.response.JwtResponse;
import ra.edu.dto.response.UserResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    UserResponse getCurrentUser();
    void logout(String token);
}
