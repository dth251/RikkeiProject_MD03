package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.CourseCreateRequest;
import ra.edu.dto.request.CourseStatusUpdateRequest;
import ra.edu.dto.request.CourseUpdateRequest;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.CourseResponse;
import ra.edu.dto.response.PageResponse;
import ra.edu.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false, name = "teacher_id") Long teacherId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "courseId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            status = "PUBLISHED";
        }
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourses(search, teacherId, status, pageable), "Lấy danh sách khóa học thành công"));
    }

    @GetMapping("/{course_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable("course_id") Long courseId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(courseId), "Lấy thông tin khóa học thành công"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(courseService.createCourse(request), "Tạo khóa học thành công"));
    }

    @PutMapping("/{course_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(courseId, request), "Cập nhật khóa học thành công"));
    }

    @PutMapping("/{course_id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourseStatus(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody CourseStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourseStatus(courseId, request), "Cập nhật trạng thái khóa học thành công"));
    }

    @DeleteMapping("/{course_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable("course_id") Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa khóa học thành công"));
    }
}
