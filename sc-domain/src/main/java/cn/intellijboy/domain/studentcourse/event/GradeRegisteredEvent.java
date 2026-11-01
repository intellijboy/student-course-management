/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.studentcourse.event;

import cn.intellijboy.domain.event.DomainEvent;

/**
 * 成绩登记领域事件。
 */
public record GradeRegisteredEvent(Long studentId, Long courseId, int score) implements DomainEvent {
}
