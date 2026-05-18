package ra.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Enrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentUsername(String username);
    List<Enrollment> findByStudentUserId(Long studentId);
    Optional<Enrollment> findByEnrollmentIdAndStudentUsername(Long enrollmentId, String username);
    boolean existsByStudentUserIdAndCourseCourseId(Long studentId, Long courseId);
}
