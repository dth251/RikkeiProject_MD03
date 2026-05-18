package ra.edu.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c WHERE " +
           "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:teacherId IS NULL OR c.teacher.userId = :teacherId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Course> searchCourses(@Param("keyword") String keyword, @Param("teacherId") Long teacherId, @Param("status") ra.edu.entity.CourseStatus status, Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN c.enrollments e GROUP BY c ORDER BY COUNT(e) DESC")
    Page<Course> findTopCourses(Pageable pageable);

    java.util.List<Course> findByTeacherUserId(Long teacherId);
}
