package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ra.edu.dto.response.StudentProgressReportResponse;
import ra.edu.dto.response.TeacherCourseOverviewResponse;
import ra.edu.dto.response.TopCourseResponse;
import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;
import ra.edu.repository.CourseRepository;
import ra.edu.repository.EnrollmentRepository;
import ra.edu.service.ReportService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<TopCourseResponse> getTopCourses(int limit) {
        Page<Course> page = courseRepository.findTopCourses(PageRequest.of(0, limit));
        return page.getContent().stream()
                .map(course -> TopCourseResponse.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .price(course.getPrice())
                        .totalEnrollments(course.getEnrollments() != null ? course.getEnrollments().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentProgressReportResponse> getStudentProgress(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findByStudentUserId(studentId);
        return enrollments.stream()
                .map(enrollment -> StudentProgressReportResponse.builder()
                        .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null)
                        .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                        .status(enrollment.getStatus())
                        .progressPercentage(enrollment.getProgressPercentage())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherCourseOverviewResponse> getTeacherCoursesOverview(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherUserId(teacherId);
        return courses.stream()
                .map(course -> {
                    int studentsCount = course.getEnrollments() != null ? course.getEnrollments().size() : 0;
                    BigDecimal estimatedRevenue = course.getPrice() != null ? course.getPrice().multiply(BigDecimal.valueOf(studentsCount)) : BigDecimal.ZERO;
                    
                    return TeacherCourseOverviewResponse.builder()
                            .courseId(course.getCourseId())
                            .title(course.getTitle())
                            .price(course.getPrice())
                            .totalStudents(studentsCount)
                            .estimatedRevenue(estimatedRevenue)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
