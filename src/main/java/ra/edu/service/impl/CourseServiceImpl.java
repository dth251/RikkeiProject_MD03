package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.CourseCreateRequest;
import ra.edu.dto.request.CourseStatusUpdateRequest;
import ra.edu.dto.request.CourseUpdateRequest;
import ra.edu.dto.response.CourseResponse;
import ra.edu.dto.response.PageResponse;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.config.exception.BadRequestException;
import ra.edu.config.exception.ConflictException;
import ra.edu.entity.Course;
import ra.edu.entity.CourseStatus;
import ra.edu.entity.Role;
import ra.edu.entity.User;
import ra.edu.mapper.CourseMapper;
import ra.edu.repository.CourseRepository;
import ra.edu.repository.UserRepository;
import ra.edu.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;

    @Override
    public PageResponse<CourseResponse> getCourses(String keyword, Long teacherId, String status, Pageable pageable) {
        if (teacherId != null) {
            User teacher = userRepository.findById(teacherId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên với ID: " + teacherId));
            if (teacher.getRole() != Role.TEACHER) {
                throw new BadRequestException("Người dùng với ID " + teacherId + " không phải là giảng viên");
            }
        }

        CourseStatus courseStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                courseStatus = CourseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Trạng thái khóa học không hợp lệ! Chỉ chấp nhận DRAFT, PUBLISHED, hoặc ARCHIVED.");
            }
        }
        Page<Course> page = courseRepository.searchCourses(keyword, teacherId, courseStatus, pageable);
        List<CourseResponse> responses = page.getContent().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
        return PageResponse.of(page, responses);
    }

    @Override
    public CourseResponse getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse createCourse(CourseCreateRequest request) {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giảng viên"));
        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("Người dùng được chọn không phải là giảng viên");
        }
        Course course = courseMapper.toEntity(request, teacher);
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setDurationHours(request.getDurationHours());
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse updateCourseStatus(Long courseId, CourseStatusUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        course.setStatus(request.getStatus());
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));
        if (course.getEnrollments() != null && !course.getEnrollments().isEmpty()) {
            throw new ConflictException("Không thể xóa khóa học đã có học viên đăng ký");
        }
        courseRepository.deleteById(courseId);
    }
}
