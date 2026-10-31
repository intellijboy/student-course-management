/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.librarylending.domain.exception;

/**
 * 读者重复预约同一图书。
 */
public class DuplicateReservationException extends RuntimeException {

    /**
     * 创建重复预约异常。
     *
     * @param message 异常描述信息
     */
    public DuplicateReservationException(String message) {
        super(message);
    }
}
