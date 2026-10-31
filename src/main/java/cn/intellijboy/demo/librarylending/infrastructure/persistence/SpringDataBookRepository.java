/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 图书 Spring Data 仓库接口：基于 Spring Data JPA 提供 {@link BookJpaEntity} 的标准 CRUD 与分页等能力。
 */
public interface SpringDataBookRepository extends JpaRepository<BookJpaEntity, Long> {
}
