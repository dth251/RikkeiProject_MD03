package ra.edu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseUpdateRequest {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;

    private String description;

    @DecimalMin(value = "0.0", message = "Giá khóa học không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Thời lượng khóa học không được âm")
    private Integer durationHours;
}
