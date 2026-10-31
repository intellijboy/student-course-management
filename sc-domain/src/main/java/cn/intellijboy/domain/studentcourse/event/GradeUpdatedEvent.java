/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.studentcourse.event;

/**
 * 成绩修改领域事件。
 */
public record GradeUpdatedEvent(Long id, Long studentId, Long courseId, int score) {
}
