/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.infrastructure.librarylending.mapper;

import cn.intellijboy.domain.librarylending.model.BookId;
import cn.intellijboy.domain.librarylending.model.ReaderId;
import cn.intellijboy.domain.librarylending.model.Reservation;
import cn.intellijboy.infrastructure.librarylending.persistence.ReservationJpaEntity;
import org.springframework.stereotype.Component;

/**
 * 预约记录映射器：负责领域模型 {@link Reservation} 与 JPA 实体 {@link ReservationJpaEntity} 之间的双向转换。
 */
@Component
public class ReservationMapper {

    public ReservationJpaEntity toJpa(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.setId(reservation.getId());
        entity.setReaderId(reservation.getReaderId().value());
        entity.setBookId(reservation.getBookId().value());
        entity.setStatus(reservation.getStatus());
        entity.setCreatedAt(reservation.getCreatedAt());
        return entity;
    }

    public Reservation toDomain(ReservationJpaEntity entity) {
        return new Reservation(entity.getId(), new ReaderId(entity.getReaderId()),
            new BookId(entity.getBookId()), entity.getStatus(), entity.getCreatedAt());
    }
}
