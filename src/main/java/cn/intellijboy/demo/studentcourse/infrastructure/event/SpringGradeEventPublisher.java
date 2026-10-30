package cn.intellijboy.demo.studentcourse.infrastructure.event;

import cn.intellijboy.demo.studentcourse.domain.event.GradeEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 成绩领域事件发布适配器：基于 Spring ApplicationEventPublisher 同步发布（仅作示范，不引入跨模块消费）。
 */
@Component
public class SpringGradeEventPublisher implements GradeEventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringGradeEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(Object event) {
        publisher.publishEvent(event);
    }
}
