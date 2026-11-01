/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2026 Intellijboy
 */
package cn.intellijboy.domain.event;

/**
 * 领域事件发布端口：领域层定义，由基础设施层实现（如 Spring 事件总线或消息中间件）。
 *
 * <p>跨限界上下文共享：各上下文的具体领域事件（{@link DomainEvent} 实现）均通过本端口发布，
 * 避免每个上下文重复定义各自的发布端口与 Spring 适配器。
 */
public interface DomainEventPublisher {

    /**
     * 发布领域事件。
     *
     * @param event 待发布的领域事件，不能为 {@code null}
     */
    void publish(DomainEvent event);
}
