package ra.edu.service;

import ra.edu.dto.request.LessonCreateRequest;
import ra.edu.dto.request.LessonPublishRequest;
import ra.edu.dto.request.LessonUpdateRequest;
import ra.edu.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getLessonsByCourseId(Long courseId, boolean isTeacherOrAdmin);
    LessonResponse getLessonById(Long lessonId, boolean isTeacherOrAdmin);
    LessonResponse getLessonPreview(Long lessonId, boolean isTeacherOrAdmin);
    LessonResponse createLesson(Long courseId, LessonCreateRequest request, String username, boolean isAdmin);
    LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request, String username, boolean isAdmin);
    LessonResponse publishLesson(Long lessonId, LessonPublishRequest request, String username, boolean isAdmin);
    void deleteLesson(Long lessonId, String username, boolean isAdmin);
}
