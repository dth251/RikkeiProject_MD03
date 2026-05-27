package ra.edu.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;

    private String description;

    @NotNull(message = "ID Giảng viên không được để trống")
    private Long teacherId;

    @DecimalMin(value = "0.0", message = "Giá khóa học không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Thời lượng khóa học không được âm")
    private Integer durationHours;
}
