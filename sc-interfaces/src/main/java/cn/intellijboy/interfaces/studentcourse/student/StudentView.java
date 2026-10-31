/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.interfaces.studentcourse.student;

/**
 * 学生视图：承载学生标识、姓名与邮箱。
 */
public record StudentView(Long id, String name, String email) {
}
