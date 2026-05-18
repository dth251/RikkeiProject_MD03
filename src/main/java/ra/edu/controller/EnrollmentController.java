package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.EnrollmentCreateRequest;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.EnrollmentResponse;
import ra.edu.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                enrollmentService.getMyEnrollments(auth.getName()), 
                "Lấy danh sách khóa học đã đăng ký thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(@Valid @RequestBody EnrollmentCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.status(201).body(ApiResponse.success(
                enrollmentService.enrollCourse(request, auth.getName()), 
                "Đăng ký khóa học thành công"));
    }

    @GetMapping("/{enrollment_id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentDetail(@PathVariable("enrollment_id") Long enrollmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                enrollmentService.getEnrollmentDetail(enrollmentId, auth.getName()), 
                "Lấy chi tiết tiến độ thành công"));
    }

    @PutMapping("/{enrollment_id}/complete_lesson/{lesson_id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> completeLesson(
            @PathVariable("enrollment_id") Long enrollmentId,
            @PathVariable("lesson_id") Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                enrollmentService.completeLesson(enrollmentId, lessonId, auth.getName()), 
                "Đánh dấu hoàn thành bài học thành công"));
    }
}
