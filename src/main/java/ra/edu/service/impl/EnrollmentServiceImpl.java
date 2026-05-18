package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.EnrollmentCreateRequest;
import ra.edu.dto.response.EnrollmentResponse;
import ra.edu.entity.*;
import ra.edu.config.exception.ConflictException;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.mapper.EnrollmentMapper;
import ra.edu.repository.*;
import ra.edu.service.EnrollmentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public List<EnrollmentResponse> getMyEnrollments(String username) {
        return enrollmentRepository.findByStudentUsername(username).stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponse enrollCourse(EnrollmentCreateRequest request, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        if (!course.getStatus().equals(CourseStatus.PUBLISHED)) {
            throw new ConflictException("Khóa học chưa được xuất bản");
        }

        if (enrollmentRepository.existsByStudentUserIdAndCourseCourseId(student.getUserId(), course.getCourseId())) {
            throw new ConflictException("Bạn đã đăng ký khóa học này rồi");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment = enrollmentRepository.save(enrollment);

        List<Lesson> publishedLessons = lessonRepository.findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(course.getCourseId());
        
        for (Lesson lesson : publishedLessons) {
            LessonProgress progress = new LessonProgress();
            progress.setEnrollment(enrollment);
            progress.setLesson(lesson);
            lessonProgressRepository.save(progress);
        }

        return enrollmentMapper.toResponse(enrollmentRepository.findById(enrollment.getEnrollmentId()).get());
    }

    @Override
    public EnrollmentResponse getEnrollmentDetail(Long enrollmentId, String username) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentIdAndStudentUsername(enrollmentId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đăng ký"));
        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    public EnrollmentResponse completeLesson(Long enrollmentId, Long lessonId, String username) {
        Enrollment enrollment = enrollmentRepository.findByEnrollmentIdAndStudentUsername(enrollmentId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin đăng ký"));

        LessonProgress progress = lessonProgressRepository.findByEnrollmentEnrollmentIdAndLessonLessonId(enrollmentId, lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tiến độ bài học này"));

        if (!progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(progress);

            int totalLessons = lessonRepository.findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(enrollment.getCourse().getCourseId()).size();
            int completedLessons = lessonProgressRepository.countByEnrollmentEnrollmentIdAndIsCompletedTrue(enrollmentId);

            BigDecimal percentage = BigDecimal.valueOf((double) completedLessons / totalLessons * 100)
                    .setScale(2, RoundingMode.HALF_UP);
            
            enrollment.setProgressPercentage(percentage);
            
            if (completedLessons == totalLessons) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletionDate(LocalDateTime.now());
            }

            enrollment = enrollmentRepository.save(enrollment);
        }
        return enrollmentMapper.toResponse(enrollment);
    }
}
