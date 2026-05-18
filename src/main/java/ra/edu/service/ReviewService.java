package ra.edu.service;

import ra.edu.dto.request.ReviewCreateRequest;
import ra.edu.dto.request.ReviewUpdateRequest;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.ReviewResponse;

public interface ReviewService {
    PageResponse<ReviewResponse> getCourseReviews(Long courseId, int page, int size);
    ReviewResponse createReview(Long courseId, ReviewCreateRequest request, String username);
    ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request, String username, boolean isAdmin);
    void deleteReview(Long reviewId, String username, boolean isAdmin);
}
