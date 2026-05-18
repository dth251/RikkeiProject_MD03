package ra.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ra.edu.entity.CourseStatus;

@Data
public class CourseStatusUpdateRequest {
    @NotNull(message = "Trạng thái không được để trống")
    private CourseStatus status;
}
