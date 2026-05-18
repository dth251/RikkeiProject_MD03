package ra.edu.mapper;

import org.springframework.stereotype.Component;
import ra.edu.dto.request.ReviewCreateRequest;
import ra.edu.dto.response.ReviewResponse;
import ra.edu.entity.Course;
import ra.edu.entity.Review;
import ra.edu.entity.User;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) return null;
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .courseId(review.getCourse() != null ? review.getCourse().getCourseId() : null)
                .studentId(review.getStudent() != null ? review.getStudent().getUserId() : null)
                .studentName(review.getStudent() != null ? review.getStudent().getFullName() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public Review toEntity(ReviewCreateRequest request, Course course, User student) {
        if (request == null) return null;
        Review review = new Review();
        review.setCourse(course);
        review.setStudent(student);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return review;
    }
}
