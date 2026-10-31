/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.reservation;

import cn.intellijboy.domain.librarylending.model.Reservation;

import java.time.LocalDateTime;

/**
 * 预约视图：承载预约标识、读者、状态及创建时间。
 */
public record ReservationView(Long id, Long readerId, String status, LocalDateTime createdAt) {
    public static ReservationView of(Reservation r) {
        return new ReservationView(r.getId(), r.getReaderId().value(), r.getStatus().name(), r.getCreatedAt());
    }
}
