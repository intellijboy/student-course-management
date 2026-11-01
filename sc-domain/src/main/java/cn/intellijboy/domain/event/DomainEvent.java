/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.event;

/**
 * 领域事件统一标识接口：跨限界上下文共享的领域事件基类型。
 *
 * <p>各限界上下文的具体领域事件（如借出、归还、成绩登记等）实现本接口，
 * 由共享的 {@link DomainEventPublisher} 发布。
 */
public interface DomainEvent {
}
