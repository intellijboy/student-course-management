package cn.intellijboy.demo.librarylending.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类：携带事件发生时间。
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredAt;

    protected DomainEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    protected DomainEvent(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
