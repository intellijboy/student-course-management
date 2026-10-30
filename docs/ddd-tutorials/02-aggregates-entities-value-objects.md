# 02 聚合根、实体与值对象

> 配套代码：`cn.intellijboy.demo.librarylending.domain.model`

## 聚合根与聚合边界

聚合（Aggregate）是一组以聚合根为入口、必须保持一致性的对象集合。外部只能通过聚合根修改其内部对象。

`librarylending` 划分了三个聚合根：

| 聚合根 | 含义 | 内部成员 |
| --- | --- | --- |
| `Book` | 书目与馆藏 | `BookCopy` 实体（副本状态） |
| `Loan` | 借阅记录 | 无子实体，自持借期/逾期状态 |
| `Reservation` | 预约 | 无子实体，自持排队状态 |

**Book 聚合示例**：`Book` 的副本集合只能通过聚合根操作，外部不能直接改 `BookCopy` 状态：

```java
public class Book {
    private final List<BookCopy> copies;

    public Optional<BookCopy> firstAvailableCopy() { ... }   // 对外只读查询
    public void addCopy(BookCopy copy) { copies.add(copy); } // 增补馆藏（聚合内操作）
}
```

## 实体与值对象

- **实体（Entity）**：有身份标识、可变的领域对象。`BookCopy` 通过 `BookCopyId` 标识，状态可变（`borrow`/`returnCopy`）。
- **值对象（Value Object）**：无标识、不可变、靠值相等。本工程大量使用 Java `record`：

```java
public record BookId(Long value) {
    public BookId {
        if (value == null) throw new IllegalArgumentException("图书 id 不能为空");
    }
}

public record OverdueFee(BigDecimal ratePerDay) {
    public OverdueFee {
        if (ratePerDay == null || ratePerDay.signum() < 0)
            throw new IllegalArgumentException("逾期费率不能为空且不能为负");
    }
    public BigDecimal amountFor(int overdueDays) { ... } // 行为内聚于值对象
}
```

**值对象的意义**：把"是什么"与"怎么算"收进对象本身，避免散落在大段的 `if` 中。`OverdueFee.amountFor(overdueDays)` 让逾期费用计算可独立测试。

## 不变式由聚合方法保证

状态流转只在聚合内进行，并拒绝非法流转：

```java
public class BookCopy {
    public void borrow() {
        requireAvailable("副本不可借出"); // AVAILABLE -> BORROWED
        this.status = BookCopyStatus.BORROWED;
    }
    public void returnToReserved() {
        if (this.status != BookCopyStatus.BORROWED) throw new BookCopyUnavailableException("...");
        this.status = BookCopyStatus.RESERVED; // BORROWED -> RESERVED（归还给预约者）
    }
}
```

测试见 `BookTest`、`LoanTest`、`ReservationTest`——它们直接验证聚合的领域行为，无需 Spring。

## 小结

- 聚合根承载不变式与状态流转；值对象承载"值的语义"。
- 这些纯领域类没有 Spring 注解，测试直接 `new` 即可。
