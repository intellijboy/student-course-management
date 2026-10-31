/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.persistence;

import cn.intellijboy.domain.librarylending.model.BookCopyStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 图书副本 JPA 实体：对应 book_copies 表，持久化单本图书副本及其借阅状态。
 */
@Entity
@Table(name = "book_copies")
public class BookCopyJpaEntity {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookCopyStatus status;

    public BookCopyJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }
}
