package cn.intellijboy.demo.librarylending.infrastructure.event;

import cn.intellijboy.demo.librarylending.domain.event.DomainEvent;
import cn.intellijboy.demo.librarylending.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布适配器：基于 Spring ApplicationEventPublisher 同步发布。
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
}
