package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.LessonCreateRequest;
import ra.edu.dto.request.LessonPublishRequest;
import ra.edu.dto.request.LessonUpdateRequest;
import ra.edu.dto.response.LessonResponse;
import ra.edu.entity.Course;
import ra.edu.entity.Lesson;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.mapper.LessonMapper;
import ra.edu.repository.CourseRepository;
import ra.edu.repository.LessonRepository;
import ra.edu.service.LessonService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    @Override
    public List<LessonResponse> getLessonsByCourseId(Long courseId, boolean isTeacherOrAdmin) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học");
        }

        List<Lesson> lessons;
        if (isTeacherOrAdmin) {
            lessons = lessonRepository.findByCourseCourseIdOrderByOrderIndexAsc(courseId);
        } else {
            lessons = lessonRepository.findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId);
        }

        return lessons.stream().map(lessonMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public LessonResponse getLessonById(Long lessonId, boolean isTeacherOrAdmin) {
        Lesson lesson;
        if (isTeacherOrAdmin) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
        } else {
            lesson = lessonRepository.findByLessonIdAndIsPublishedTrue(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học hoặc chưa được xuất bản"));
        }
        return lessonMapper.toResponse(lesson);
    }

    @Override
    public LessonResponse getLessonPreview(Long lessonId, boolean isTeacherOrAdmin) {
        Lesson lesson;
        if (isTeacherOrAdmin) {
            lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));
        } else {
            lesson = lessonRepository.findByLessonIdAndIsPublishedTrue(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học hoặc chưa được xuất bản"));
        }
        LessonResponse response = lessonMapper.toResponse(lesson);
        if (response.getTextContent() != null && response.getTextContent().length() > 50) {
            response.setTextContent(response.getTextContent().substring(0, 50) + "...");
        }
        response.setContentUrl(null);
        return response;
    }

    private void verifyTeacherOrAdmin(Course course, String username, boolean isAdmin) {
        if (!isAdmin && !course.getTeacher().getUsername().equals(username)) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này");
        }
    }

    @Override
    public LessonResponse createLesson(Long courseId, LessonCreateRequest request, String username, boolean isAdmin) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        verifyTeacherOrAdmin(course, username, isAdmin);

        int count = lessonRepository.countByCourseCourseId(courseId);
        Lesson lesson = lessonMapper.toEntity(request, course, count + 1);
        lesson = lessonRepository.save(lesson);

        return lessonMapper.toResponse(lesson);
    }

    @Override
    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request, String username, boolean isAdmin) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        verifyTeacherOrAdmin(lesson.getCourse(), username, isAdmin);

        lesson.setTitle(request.getTitle());
        lesson.setContentUrl(request.getContentUrl());
        lesson.setTextContent(request.getTextContent());
        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }

        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    public LessonResponse publishLesson(Long lessonId, LessonPublishRequest request, String username, boolean isAdmin) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        verifyTeacherOrAdmin(lesson.getCourse(), username, isAdmin);

        lesson.setIsPublished(request.getIsPublished());
        lesson = lessonRepository.save(lesson);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    public void deleteLesson(Long lessonId, String username, boolean isAdmin) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học"));

        verifyTeacherOrAdmin(lesson.getCourse(), username, isAdmin);

        lessonRepository.delete(lesson);
    }
}
