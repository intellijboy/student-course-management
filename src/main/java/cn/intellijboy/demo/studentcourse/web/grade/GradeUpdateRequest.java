/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.web.grade;

/**
 * 成绩更新请求：承载更新后的分数。
 */
public record GradeUpdateRequest(Integer score) {
}
