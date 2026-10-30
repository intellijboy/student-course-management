package cn.intellijboy.demo.domain.studentcourse.event;

/**
 * 成绩领域事件发布端口：领域层定义，由基础设施层实现。
 */
public interface GradeEventPublisher {

    void publish(Object event);
}
