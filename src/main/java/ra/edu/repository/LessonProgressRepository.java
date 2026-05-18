package ra.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.entity.LessonProgress;

import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByEnrollmentEnrollmentIdAndLessonLessonId(Long enrollmentId, Long lessonId);
    int countByEnrollmentEnrollmentIdAndIsCompletedTrue(Long enrollmentId);
}
