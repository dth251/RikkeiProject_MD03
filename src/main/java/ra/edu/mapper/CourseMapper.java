package ra.edu.mapper;

import org.springframework.stereotype.Component;
import ra.edu.dto.request.CourseCreateRequest;
import ra.edu.dto.response.CourseResponse;
import ra.edu.dto.response.LessonResponse;
import ra.edu.entity.Course;
import ra.edu.entity.User;

@Component
@lombok.RequiredArgsConstructor
public class CourseMapper {
    private final LessonMapper lessonMapper;

    public CourseResponse toResponse(Course course) {
        if (course == null) return null;

        java.util.List<LessonResponse> lessonResponses = null;
        if (course.getLessons() != null) {
            lessonResponses = course.getLessons().stream()
                    .filter(l -> l.getIsPublished() != null && l.getIsPublished())
                    .sorted(java.util.Comparator.comparing(l -> l.getOrderIndex() != null ? l.getOrderIndex() : 0))
                    .map(lessonMapper::toResponse)
                    .collect(java.util.stream.Collectors.toList());
        }

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacher() != null ? course.getTeacher().getUserId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .price(course.getPrice())
                .durationHours(course.getDurationHours())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .lessons(lessonResponses)
                .build();
    }

    public Course toEntity(CourseCreateRequest request, User teacher) {
        if (request == null) return null;
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        course.setPrice(request.getPrice());
        course.setDurationHours(request.getDurationHours());
        return course;
    }
}
