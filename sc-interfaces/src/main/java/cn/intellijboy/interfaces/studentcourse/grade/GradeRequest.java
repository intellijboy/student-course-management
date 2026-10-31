/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.studentcourse.grade;

/**
 * 成绩登记请求：承载学生/课程标识与分数。
 */
public record GradeRequest(Long studentId, Long courseId, Integer score) {
}
