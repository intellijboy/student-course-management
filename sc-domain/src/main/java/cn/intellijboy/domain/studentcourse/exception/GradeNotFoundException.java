/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.studentcourse.exception;

/**
 * 成绩不存在。
 */
public class GradeNotFoundException extends RuntimeException {

    /**
     * 创建成绩不存在异常。
     *
     * @param message 异常描述信息
     */
    public GradeNotFoundException(String message) {
        super(message);
    }
}
