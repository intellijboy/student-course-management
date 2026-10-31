/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.web.course;

/**
 * 课程视图：承载课程标识、名称与学分。
 */
public record CourseView(Long id, String name, Integer credits) {
}
