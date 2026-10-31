/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.librarylending.exception;

/**
 * 图书不存在。
 */
public class BookNotFoundException extends RuntimeException {

    /**
     * 创建图书不存在异常。
     *
     * @param message 异常描述信息
     */
    public BookNotFoundException(String message) {
        super(message);
    }
}
