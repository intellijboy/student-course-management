package cn.intellijboy.demo.librarylending.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类：携带事件发生时间。
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredAt;

    /**
     * 创建领域事件，事件发生时间取当前系统时间。
     */
    protected DomainEvent() {
        this.occurredAt = LocalDateTime.now();
    }

    /**
     * 创建指定发生时间的领域事件。
     *
     * @param occurredAt 事件发生时间
     */
    protected DomainEvent(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
