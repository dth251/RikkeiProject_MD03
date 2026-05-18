package ra.edu.mapper;

import org.springframework.stereotype.Component;
import ra.edu.dto.response.EnrollmentResponse;
import ra.edu.dto.response.LessonProgressResponse;
import ra.edu.entity.Enrollment;

import java.util.stream.Collectors;

@Component
public class EnrollmentMapper {

    public EnrollmentResponse toResponse(Enrollment enrollment) {
        if (enrollment == null) return null;
        return EnrollmentResponse.builder()
                .enrollmentId(enrollment.getEnrollmentId())
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .progresses(enrollment.getLessonProgresses() != null ?
                        enrollment.getLessonProgresses().stream().map(p -> LessonProgressResponse.builder()
                                .lessonId(p.getLesson() != null ? p.getLesson().getLessonId() : null)
                                .title(p.getLesson() != null ? p.getLesson().getTitle() : null)
                                .isCompleted(p.getIsCompleted())
                                .completedAt(p.getCompletedAt())
                                .lastAccessedAt(p.getLastAccessedAt())
                                .build()).collect(Collectors.toList())
                        : null)
                .build();
    }
}
