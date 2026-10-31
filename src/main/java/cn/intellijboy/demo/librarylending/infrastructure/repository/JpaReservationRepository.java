/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.infrastructure.repository;

import cn.intellijboy.demo.librarylending.domain.model.BookId;
import cn.intellijboy.demo.librarylending.domain.model.Reservation;
import cn.intellijboy.demo.librarylending.domain.model.ReservationStatus;
import cn.intellijboy.demo.librarylending.domain.model.ReaderId;
import cn.intellijboy.demo.librarylending.domain.repository.ReservationRepository;
import cn.intellijboy.demo.librarylending.infrastructure.mapper.ReservationMapper;
import cn.intellijboy.demo.librarylending.infrastructure.persistence.SpringDataReservationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 预约记录仓库的 JPA 实现：通过 Spring Data 的 {@link SpringDataReservationRepository} 查询并保存预约记录，并由 {@link ReservationMapper} 完成领域模型与实体互转。
 */
@Repository
public class JpaReservationRepository implements ReservationRepository {

    private final SpringDataReservationRepository springDataRepository;
    private final ReservationMapper mapper;

    public JpaReservationRepository(SpringDataReservationRepository springDataRepository, ReservationMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return mapper.toDomain(springDataRepository.save(mapper.toJpa(reservation)));
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return springDataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findByBookOrderedByCreatedAt(BookId bookId) {
        return springDataRepository.findByBookIdOrderByCreatedAtAsc(bookId.value()).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public List<Reservation> findWaitingByBookOrderedByCreatedAt(BookId bookId) {
        return springDataRepository
            .findByBookIdAndStatusOrderByCreatedAtAsc(bookId.value(), ReservationStatus.WAITING).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsWaitingByReaderAndBook(ReaderId readerId, BookId bookId) {
        return springDataRepository.existsByReaderIdAndBookIdAndStatus(
            readerId.value(), bookId.value(), ReservationStatus.WAITING);
    }
}
