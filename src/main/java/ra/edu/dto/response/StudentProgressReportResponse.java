package ra.edu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.edu.entity.EnrollmentStatus;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressReportResponse {
    private Long courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private BigDecimal progressPercentage;
}
