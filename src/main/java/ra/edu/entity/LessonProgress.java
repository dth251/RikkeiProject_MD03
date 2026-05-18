package ra.edu.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        // Đảm bảo mỗi bài học chỉ có một bản ghi tiến độ cho mỗi lượt đăng ký
        @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
})
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    // Khóa ngoại liên kết với bảng Enrollments
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", referencedColumnName = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    // Khóa ngoại liên kết với bảng Lessons
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", referencedColumnName = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    // Thiết lập giá trị mặc định khi tạo mới bản ghi lần đầu
    @PrePersist
    public void prePersist() {
        if (this.isCompleted == null) {
            this.isCompleted = false;
        }
    }
}
