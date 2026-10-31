/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.mapper;

import cn.intellijboy.domain.librarylending.model.Book;
import cn.intellijboy.domain.librarylending.model.BookCopy;
import cn.intellijboy.domain.librarylending.model.BookCopyId;
import cn.intellijboy.domain.librarylending.model.BookId;
import cn.intellijboy.infrastructure.librarylending.persistence.BookCopyJpaEntity;
import cn.intellijboy.infrastructure.librarylending.persistence.BookJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 图书映射器：负责领域模型 {@link Book} 与 JPA 持久化实体（{@link BookJpaEntity}、{@link BookCopyJpaEntity}）之间的双向转换。
 */
@Component
public class BookMapper {

    public BookJpaEntity toJpa(Book book) {
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(book.getId().value());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setCopies(book.getCopies().stream().map(copy -> {
            BookCopyJpaEntity copyEntity = new BookCopyJpaEntity();
            copyEntity.setId(copy.getId().value());
            copyEntity.setStatus(copy.getStatus());
            return copyEntity;
        }).toList());
        return entity;
    }

    public Book toDomain(BookJpaEntity entity) {
        List<BookCopy> copies = entity.getCopies().stream()
            .map(c -> new BookCopy(new BookCopyId(c.getId()), c.getStatus()))
            .toList();
        return new Book(new BookId(entity.getId()), entity.getTitle(), entity.getAuthor(), copies);
    }
}
