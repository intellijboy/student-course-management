/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.studentcourse.exception;

/**
 * 课程不存在。
 */
public class CourseNotFoundException extends RuntimeException {

    /**
     * 创建课程不存在异常。
     *
     * @param message 异常描述信息
     */
    public CourseNotFoundException(String message) {
        super(message);
    }
}
