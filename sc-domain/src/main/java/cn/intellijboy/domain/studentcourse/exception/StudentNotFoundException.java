/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.studentcourse.exception;

/**
 * 学生不存在。
 */
public class StudentNotFoundException extends RuntimeException {

    /**
     * 创建学生不存在异常。
     *
     * @param message 异常描述信息
     */
    public StudentNotFoundException(String message) {
        super(message);
    }
}
