package ra.edu.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments", uniqueConstraints = {
        // Đảm bảo mỗi sinh viên chỉ đăng ký một khóa học một lần
        @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
// Ràng buộc CHECK để progress_percentage luôn nằm trong khoảng 0 đến 100
@Check(constraints = "progress_percentage >= 0 AND progress_percentage <= 100")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long enrollmentId;

    // Khóa ngoại liên kết với sinh viên (bảng users)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "user_id", nullable = false)
    private User student;

    // Khóa ngoại liên kết với khóa học (bảng courses)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private Course course;

    @CreationTimestamp
    @Column(name = "enrollment_date", updatable = false, nullable = false)
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    // Sử dụng BigDecimal cho kiểu DECIMAL(5,2)
    @Column(name = "progress_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal progressPercentage;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<LessonProgress> lessonProgresses = new java.util.ArrayList<>();
    // Gán giá trị mặc định khi sinh viên vừa mới đăng ký (lưu lần đầu)
    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = EnrollmentStatus.ENROLLED;
        }
        if (this.progressPercentage == null) {
            this.progressPercentage = BigDecimal.ZERO; // Bắt đầu ở 0.00%
        }
    }
}
