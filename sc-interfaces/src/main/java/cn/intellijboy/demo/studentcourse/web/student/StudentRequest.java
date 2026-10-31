/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.web.student;

/**
 * 学生创建/更新请求：承载学生姓名与邮箱。
 */
public record StudentRequest(String name, String email) {
}
