/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.demo.studentcourse.domain.event;

/**
 * 成绩登记领域事件。
 */
public record GradeRegisteredEvent(Long studentId, Long courseId, int score) {
}
