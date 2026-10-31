/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.studentcourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 课程成绩 Spring Data 仓库接口：提供按学生与课程定位成绩的查询方法。
 */
public interface SpringDataCourseGradeRepository extends JpaRepository<CourseGradeJpaEntity, Long> {

    Optional<CourseGradeJpaEntity> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
