package cn.intellijboy.demo.studentcourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStudentRepository extends JpaRepository<StudentJpaEntity, Long> {
}
