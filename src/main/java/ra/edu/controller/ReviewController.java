package ra.edu.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.edu.config.exception.BadRequestException;
import ra.edu.dto.request.ReviewCreateRequest;
import ra.edu.dto.request.ReviewUpdateRequest;
import ra.edu.dto.response.ApiResponse;
import ra.edu.dto.response.PageResponse;
import ra.edu.dto.response.ReviewResponse;
import ra.edu.service.ReviewService;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @GetMapping("/api/courses/{course_id}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getCourseReviews(
            @PathVariable("course_id") Long courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (page < 1) {
            throw new BadRequestException("Số trang (page) phải lớn hơn hoặc bằng 1.");
        }
        if (size < 1) {
            throw new BadRequestException("Kích thước trang (size) phải lớn hơn hoặc bằng 1.");
        }
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getCourseReviews(courseId, page, size),
                "Lấy danh sách đánh giá thành công"
        ));
    }

    @PostMapping("/api/courses/{course_id}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody ReviewCreateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.status(201).body(ApiResponse.success(
                reviewService.createReview(courseId, request, auth.getName()),
                "Gửi đánh giá thành công"
        ));
    }

    @PutMapping("/api/reviews/{review_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable("review_id") Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(ApiResponse.success(
                reviewService.updateReview(reviewId, request, auth.getName(), isAdmin(auth)),
                "Cập nhật đánh giá thành công"
        ));
    }

    @DeleteMapping("/api/reviews/{review_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable("review_id") Long reviewId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        reviewService.deleteReview(reviewId, auth.getName(), isAdmin(auth));
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }
}
