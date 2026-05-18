package ra.edu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonUpdateRequest {
    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    private String contentUrl;

    private String textContent;

    private Integer orderIndex;
}
