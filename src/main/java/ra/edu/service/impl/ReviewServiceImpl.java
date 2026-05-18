package ra.edu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ra.edu.dto.request.ReviewCreateRequest;
import ra.edu.dto.request.ReviewUpdateRequest;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.ReviewResponse;
import ra.edu.entity.Course;
import ra.edu.entity.Review;
import ra.edu.entity.User;
import ra.edu.config.exception.ConflictException;
import ra.edu.config.exception.ResourceNotFoundException;
import ra.edu.mapper.ReviewMapper;
import ra.edu.repository.CourseRepository;
import ra.edu.repository.EnrollmentRepository;
import ra.edu.repository.ReviewRepository;
import ra.edu.repository.UserRepository;
import ra.edu.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public PageResponse<ReviewResponse> getCourseReviews(Long courseId, int page, int size) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học");
        }
        Page<Review> reviewPage = reviewRepository.findByCourseCourseIdOrderByCreatedAtDesc(courseId, PageRequest.of(page - 1, size));
        List<ReviewResponse> mapped = reviewPage.getContent().stream().map(reviewMapper::toResponse).collect(Collectors.toList());
        return PageResponse.of(reviewPage, mapped);
    }

    @Override
    public ReviewResponse createReview(Long courseId, ReviewCreateRequest request, String username) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học"));

        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!enrollmentRepository.existsByStudentUserIdAndCourseCourseId(student.getUserId(), courseId)) {
            throw new ConflictException("Bạn phải đăng ký và hoàn thành khóa học trước khi đánh giá");
        }

        if (reviewRepository.existsByCourseCourseIdAndStudentUserId(courseId, student.getUserId())) {
            throw new ConflictException("Bạn đã đánh giá khóa học này rồi");
        }

        Review review = reviewMapper.toEntity(request, course, student);
        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, String username, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        if (!isAdmin && !review.getStudent().getUsername().equals(username)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật đánh giá này");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    @Override
    public void deleteReview(Long reviewId, String username, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        if (!isAdmin && !review.getStudent().getUsername().equals(username)) {
            throw new AccessDeniedException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }
}
