package ra.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Lesson;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(Long courseId);

    List<Lesson> findByCourseCourseIdOrderByOrderIndexAsc(Long courseId);

    Optional<Lesson> findByLessonIdAndIsPublishedTrue(Long lessonId);

    Integer countByCourseCourseId(Long courseId);
}
