package cn.intellijboy.demo.infrastructure.studentcourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCourseRepository extends JpaRepository<CourseJpaEntity, Long> {
}
