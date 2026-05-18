package ra.edu.mapper;

import org.springframework.stereotype.Component;
import ra.edu.dto.request.LessonCreateRequest;
import ra.edu.dto.response.LessonResponse;
import ra.edu.entity.Course;
import ra.edu.entity.Lesson;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) return null;
        return LessonResponse.builder()
                .lessonId(lesson.getLessonId())
                .courseId(lesson.getCourse() != null ? lesson.getCourse().getCourseId() : null)
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentUrl())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    public Lesson toEntity(LessonCreateRequest request, Course course, int orderIndex) {
        if (request == null) return null;
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(request.getTitle());
        lesson.setContentUrl(request.getContentUrl());
        lesson.setTextContent(request.getTextContent());
        lesson.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : orderIndex);
        return lesson;
    }
}
