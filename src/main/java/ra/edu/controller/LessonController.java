package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.request.LessonCreateRequest;
import ra.edu.dto.request.LessonPublishRequest;
import ra.edu.dto.request.LessonUpdateRequest;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.LessonResponse;
import ra.edu.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    private boolean isTeacherOrAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_TEACHER"));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/courses/{course_id}/lessons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByCourseId(@PathVariable("course_id") Long courseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean teacherOrAdmin = isTeacherOrAdmin(auth);
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonsByCourseId(courseId, teacherOrAdmin), "Lấy danh sách bài học thành công"));
    }

    @GetMapping("/lessons/{lesson_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable("lesson_id") Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean teacherOrAdmin = isTeacherOrAdmin(auth);
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonById(lessonId, teacherOrAdmin), "Lấy chi tiết bài học thành công"));
    }

    @GetMapping("/lessons/{lesson_id}/content_preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonPreview(@PathVariable("lesson_id") Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean teacherOrAdmin = isTeacherOrAdmin(auth);
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonPreview(lessonId, teacherOrAdmin), "Lấy nội dung xem trước bài học thành công"));
    }

    @PostMapping("/courses/{course_id}/lessons")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody LessonCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.status(201).body(ApiResponse.success(
                lessonService.createLesson(courseId, request, auth.getName(), isAdmin(auth)),
                "Thêm bài học thành công"
        ));
    }

    @PutMapping("/lessons/{lesson_id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable("lesson_id") Long lessonId,
            @Valid @RequestBody LessonUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                lessonService.updateLesson(lessonId, request, auth.getName(), isAdmin(auth)),
                "Cập nhật bài học thành công"
        ));
    }

    @PutMapping("/lessons/{lesson_id}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> publishLesson(
            @PathVariable("lesson_id") Long lessonId,
            @Valid @RequestBody LessonPublishRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                lessonService.publishLesson(lessonId, request, auth.getName(), isAdmin(auth)),
                "Cập nhật trạng thái hiển thị thành công"
        ));
    }

    @DeleteMapping("/lessons/{lesson_id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable("lesson_id") Long lessonId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        lessonService.deleteLesson(lessonId, auth.getName(), isAdmin(auth));
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa bài học thành công"));
    }
}
