package ra.edu.service;

import ra.edu.dto.request.EnrollmentCreateRequest;
import ra.edu.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    List<EnrollmentResponse> getMyEnrollments(String username);
    EnrollmentResponse enrollCourse(EnrollmentCreateRequest request, String username);
    EnrollmentResponse getEnrollmentDetail(Long enrollmentId, String username);
    EnrollmentResponse completeLesson(Long enrollmentId, Long lessonId, String username);
}
