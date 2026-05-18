package ra.edu.service;

import org.springframework.data.domain.Pageable;
import ra.edu.dto.request.CourseCreateRequest;
import ra.edu.dto.request.CourseStatusUpdateRequest;
import ra.edu.dto.request.CourseUpdateRequest;
import ra.edu.dto.response.CourseResponse;
import ra.edu.dto.response.PageResponse;

public interface CourseService {
    PageResponse<CourseResponse> getCourses(String keyword, Long teacherId, String status, Pageable pageable);
    CourseResponse getCourseById(Long courseId);
    CourseResponse createCourse(CourseCreateRequest request);
    CourseResponse updateCourse(Long courseId, CourseUpdateRequest request);
    CourseResponse updateCourseStatus(Long courseId, CourseStatusUpdateRequest request);
    void deleteCourse(Long courseId);
}
