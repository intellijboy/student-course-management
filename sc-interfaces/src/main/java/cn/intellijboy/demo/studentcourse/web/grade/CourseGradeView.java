/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.web.grade;

/**
 * 成绩视图：承载成绩标识、学生/课程标识及分数。
 */
public record CourseGradeView(Long id, Long studentId, Long courseId, Integer score) {
}
