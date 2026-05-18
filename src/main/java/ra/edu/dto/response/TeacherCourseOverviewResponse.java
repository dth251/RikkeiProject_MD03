package ra.edu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseOverviewResponse {
    private Long courseId;
    private String title;
    private BigDecimal price;
    private Integer totalStudents;
    private BigDecimal estimatedRevenue;
}
