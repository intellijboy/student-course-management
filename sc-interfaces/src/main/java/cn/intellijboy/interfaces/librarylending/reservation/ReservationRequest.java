/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.librarylending.reservation;

/**
 * 预约请求。
 */
public record ReservationRequest(Long readerId, Long bookId) {
}
