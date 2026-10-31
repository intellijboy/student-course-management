/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.web.book;

import cn.intellijboy.demo.librarylending.web.reservation.ReservationView;

import java.util.List;

/**
 * 图书馆藏状态视图：承载图书借阅概况（可借/已借副本数）及预约队列，用于 /api/books/{id} 查询响应。
 */
public record BookStatusView(Long id, String title, int availableCopies, int borrowedCopies,
                             List<ReservationView> reservationQueue) {
}
