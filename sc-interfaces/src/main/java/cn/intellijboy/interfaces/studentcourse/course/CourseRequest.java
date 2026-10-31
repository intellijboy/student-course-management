/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.studentcourse.course;

/**
 * 课程创建/更新请求：承载课程名称与学分。
 */
public record CourseRequest(String name, Integer credits) {
}
