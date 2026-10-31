/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 学生 Spring Data 仓库接口：基于 Spring Data JPA 提供 {@link StudentJpaEntity} 的标准 CRUD 能力。
 */
public interface SpringDataStudentRepository extends JpaRepository<StudentJpaEntity, Long> {
}
