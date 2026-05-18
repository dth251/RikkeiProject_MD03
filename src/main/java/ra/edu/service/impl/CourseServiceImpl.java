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
import ra.edu.entity.Course;
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
        ra.edu.entity.CourseStatus courseStatus = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                courseStatus = ra.edu.entity.CourseStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status or handle appropriately. Here we just leave it null.
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse createCourse(CourseCreateRequest request) {
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên"));
        Course course = courseMapper.toEntity(request, teacher);
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));
        course.setStatus(request.getStatus());
        course = courseRepository.save(course);
        return courseMapper.toResponse(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new RuntimeException("Không tìm thấy khóa học");
        }
        courseRepository.deleteById(courseId);
    }
}
