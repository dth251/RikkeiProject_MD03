package ra.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ra.edu.entity.Role;

@Data
public class UserRoleUpdateRequest {
    @NotNull(message = "Role không được để trống")
    private Role role;
}
