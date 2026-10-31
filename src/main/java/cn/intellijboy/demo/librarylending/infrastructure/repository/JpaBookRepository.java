/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.infrastructure.repository;

import cn.intellijboy.demo.librarylending.domain.model.Book;
import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.repository.BookRepository;
import cn.intellijboy.demo.librarylending.infrastructure.mapper.BookMapper;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.SpringDataBookRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 图书仓库的 JPA 实现：通过 Spring Data 的 {@link SpringDataBookRepository} 持久化图书，并由 {@link BookMapper} 完成领域模型与实体互转。
 */
@Repository
public class JpaBookRepository implements BookRepository {

    private final SpringDataBookRepository springDataRepository;
    private final BookMapper mapper;

    public JpaBookRepository(SpringDataBookRepository springDataRepository, BookMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(book)));
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return springDataRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return springDataRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
