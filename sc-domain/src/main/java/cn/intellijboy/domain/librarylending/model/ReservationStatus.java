/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.librarylending.model;

/**
 * 预约状态。
 */
public enum ReservationStatus {
    /** 等待（排队中） */
    WAITING,
    /** 已通知（副本归还后按顺序转正，等待读者取书） */
    NOTIFIED,
    /** 已完成（预约读者已取书） */
    FULFILLED,
    /** 已取消 */
    CANCELLED
}
