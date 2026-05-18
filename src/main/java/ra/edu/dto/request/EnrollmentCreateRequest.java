package ra.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollmentCreateRequest {
    @NotNull(message = "ID Khóa học không được để trống")
    private Long courseId;
}
