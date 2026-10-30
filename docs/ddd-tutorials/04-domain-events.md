# 04 领域事件与事件驱动

> 配套代码：`cn.intellijboy.demo.librarylending.domain.event`

## 领域事件：描述已发生的事实

领域事件（Domain Event）描述"领域内已经发生的事"，是**事实**而非命令，命名用过去时：`BookLoanedEvent`（借出）、`BookReturnedEvent`（归还）、`ReservationPromotedEvent`（预约转正）等。

```java
public abstract class DomainEvent {
    private final LocalDateTime occurredAt; // 事件发生时间
    protected DomainEvent() { this.occurredAt = LocalDateTime.now(); }
}

public class BookLoanedEvent extends DomainEvent {
    private final ReaderId readerId;
    private final BookCopyId bookCopyId;
    // getter...
}
```

## 发布端口：领域层只定义接口

`domain.event.DomainEventPublisher` 是端口，领域逻辑只依赖它：

```java
public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
```

`infrastructure.event.SpringDomainEventPublisher` 是适配器，基于 Spring `ApplicationEventPublisher` 同步发布。换用消息中间件时，只需替换该适配器。

## 事件驱动的状态流转：归还 → 预约转正

核心示例在归还用例：归还副本后，若有等待预约，则按顺序转正。事件在此扮演"跨聚合协调"角色。

```java
// LoanApplicationService.returnCopy 中
Optional<Reservation> promoted = reservationPromotionService.promoteNextFor(bookId);
if (promoted.isPresent()) {
    copy.returnToReserved();          // 副本归还并转为预约占用
} else {
    copy.returnCopy();                // 无预约，恢复可借
}
publisher.publish(new BookReturnedEvent(loan.getReaderId(), copyId));
if (wasOverdue) {
    publisher.publish(new LoanOverdueEvent(loan.getReaderId(), copyId));
}
```

- `ReservationPromotionService` 负责"转正最早等待预约"并发布 `ReservationPromotedEvent`。
- 事件让后续行为（通知读者、统计）可以异步扩展，而不侵入聚合逻辑。

## 事件在 studentcourse 的示范

现有 `studentcourse` 模块也补充了基础事件示范（`GradeRegisteredEvent`、`GradeUpdatedEvent`），在成绩登记/修改后发布，**不引入跨模块消费**，用于演示事件发布的最小形态。

## 小结

- 领域事件是事实记录，端口定义在领域层，实现在外层。
- 事件驱动适合"跨聚合/跨模块的后续行为"，单体示例用同步发布，生产可替换为消息中间件。
