package ra.edu.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonCreateRequest {
    @NotBlank(message = "Tiêu đề bài học không được để trống")
    private String title;

    private String contentUrl;

    private String textContent;

    @Min(value = 0, message = "Thứ tự bài học không được âm")
    private Integer orderIndex;
}
