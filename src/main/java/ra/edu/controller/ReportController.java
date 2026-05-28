package ra.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ra.edu.config.exception.BadRequestException;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.StudentProgressReportResponse;
import ra.edu.dto.response.TeacherCourseOverviewResponse;
import ra.edu.dto.response.TopCourseResponse;
import ra.edu.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top_courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopCourseResponse>>> getTopCourses(@RequestParam(defaultValue = "10") int limit) {
        if (limit < 1) {
            throw new BadRequestException("Giới hạn (limit) phải lớn hơn hoặc bằng 1.");
        }
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getTopCourses(limit),
                "Lấy danh sách khóa học phổ biến nhất thành công"
        ));
    }

    @GetMapping("/student_progress/{student_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentProgressReportResponse>>> getStudentProgress(@PathVariable("student_id") Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getStudentProgress(studentId),
                "Lấy tiến độ học của sinh viên thành công"
        ));
    }

    @GetMapping("/teacher_courses_overview/{teacher_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TeacherCourseOverviewResponse>>> getTeacherCoursesOverview(@PathVariable("teacher_id") Long teacherId) {
        return ResponseEntity.ok(ApiResponse.success(
                reportService.getTeacherCoursesOverview(teacherId),
                "Lấy tổng quan khóa học của giảng viên thành công"
        ));
    }
}
