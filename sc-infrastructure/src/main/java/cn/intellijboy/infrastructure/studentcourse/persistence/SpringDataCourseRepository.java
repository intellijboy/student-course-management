/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.studentcourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程 Spring Data 仓库接口：基于 Spring Data JPA 提供 {@link CourseJpaEntity} 的标准 CRUD 能力。
 */
public interface SpringDataCourseRepository extends JpaRepository<CourseJpaEntity, Long> {
}
