/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 预约记录 Spring Data 仓库接口：提供按图书查询预约（含等待中按创建时间升序）及去重判断等方法。
 */
public interface SpringDataReservationRepository extends JpaRepository<ReservationJpaEntity, Long> {

    List<ReservationJpaEntity> findByBookIdOrderByCreatedAtAsc(Long bookId);

    /** 等待中预约按创建时间升序 */
    List<ReservationJpaEntity> findByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, cn.intellijboy.demo.librarylending.domain.model.ReservationStatus status);

    boolean existsByReaderIdAndBookIdAndStatus(Long readerId, Long bookId,
                                               cn.intellijboy.demo.librarylending.domain.model.ReservationStatus status);
}
