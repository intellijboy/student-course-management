# 03 领域服务与工厂

> 配套代码：`cn.intellijboy.demo.librarylending.domain.service`

## 领域服务：不属于单个聚合的领域逻辑

当逻辑跨越多个聚合或属于领域规则而非某个实体时，用领域服务承载。`BorrowingPolicy` 即借阅策略：

```java
public class BorrowingPolicy {
    public static final LoanPeriod DEFAULT_LOAN_PERIOD = new LoanPeriod(14);
    public static final OverdueFee DEFAULT_OVERDUE_FEE = new OverdueFee(BigDecimal.valueOf(0.5));

    public boolean hasAvailableCopy(Book book) {
        return book.firstAvailableCopy().isPresent();
    }
    public OverdueFee overdueFee() { return DEFAULT_OVERDUE_FEE; }
}
```

`ReservationPromotionService` 是另一个领域服务：编排"归还后按预约顺序转正"，它依赖仓储端口与事件端口，但仍是纯领域逻辑：

```java
public class ReservationPromotionService {
    public Optional<Reservation> promoteNextFor(BookId bookId) {
        Optional<Reservation> next = reservationRepository
            .findWaitingByBookOrderedByCreatedAt(bookId).stream().findFirst();
        next.ifPresent(reservation -> {
            reservation.promote();
            reservationRepository.save(reservation);
            publisher.publish(new ReservationPromotedEvent(reservation.getReaderId(), bookId));
        });
        return next;
    }
}
```

## 工厂：收敛创建逻辑

`BookFactory` 集中"创建书目 + 批量生成副本"的校验与构造：

```java
public class BookFactory {
    public Book createBook(BookId id, String title, String author, int copyCount) {
        if (copyCount <= 0) throw new IllegalArgumentException("馆藏副本数量必须为正");
        Book book = new Book(id, title, author);
        for (int i = 1; i <= copyCount; i++) {
            book.addCopy(new BookCopy(new BookCopyId(id.value() * 100L + i)));
        }
        return book;
    }
}
```

## 小结

- 领域服务承载跨聚合的领域规则；工厂收敛创建逻辑与校验。
- 这些纯领域类没有 Spring 注解，通过 `LibraryLendingDomainConfig` 在组合根注册为 bean；测试直接 `new` 即可（见 `BorrowingPolicyAndFactoryTest`）。
