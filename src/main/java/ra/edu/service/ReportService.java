package ra.edu.service;

import ra.edu.dto.response.StudentProgressReportResponse;
import ra.edu.dto.response.TeacherCourseOverviewResponse;
import ra.edu.dto.response.TopCourseResponse;

import java.util.List;

public interface ReportService {
    List<TopCourseResponse> getTopCourses(int limit);
    List<StudentProgressReportResponse> getStudentProgress(Long studentId);
    List<TeacherCourseOverviewResponse> getTeacherCoursesOverview(Long teacherId);
}
