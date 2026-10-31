/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.web.reservation;

/**
 * 预约请求。
 */
public record ReservationRequest(Long readerId, Long bookId) {
}
