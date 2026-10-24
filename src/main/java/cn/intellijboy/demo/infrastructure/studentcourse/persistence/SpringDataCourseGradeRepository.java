package cn.intellijboy.demo.infrastructure.studentcourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCourseGradeRepository extends JpaRepository<CourseGradeJpaEntity, Long> {

    Optional<CourseGradeJpaEntity> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
