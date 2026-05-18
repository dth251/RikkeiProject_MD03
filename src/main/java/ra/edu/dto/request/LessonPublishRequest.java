package ra.edu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonPublishRequest {
    @NotNull(message = "Trạng thái hiển thị không được để trống")
    private Boolean isPublished;
}
