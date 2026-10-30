package cn.intellijboy.demo.librarylending.domain.event;

/**
 * 领域事件发布端口：领域层定义，由基础设施层实现（如 Spring 事件总线或消息中间件）。
 */
public interface DomainEventPublisher {

    /**
     * 发布领域事件。
     *
     * @param event 待发布的领域事件
     */
    void publish(DomainEvent event);
}
