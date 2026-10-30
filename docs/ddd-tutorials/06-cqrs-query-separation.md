# 06 CQRS 查询分离

> 配套代码：`cn.intellijboy.demo.librarylending.application`

## 为什么分离读与写

命令（写）路径承载领域规则、事务与状态流转；查询（读）路径通常只需要投影数据。把两者混在一起，会让读请求也付出领域建模与聚合加载的代价，并使查询逻辑与写模型强耦合。

CQRS（Command Query Responsibility Segregation）把读与写分离：**写走领域聚合，读走独立的读模型查询**。

## 写路径：领域聚合 + 应用服务

写操作由应用服务编排聚合完成：

```java
// LoanApplicationService.borrow：命令
@Transactional
public Loan borrow(ReaderId readerId, BookId bookId) {
    requireReader(readerId);
    Book book = requireBook(bookId);
    BookCopy copy = book.firstAvailableCopy().orElseThrow(...);
    copy.borrow();
    Loan loan = loanFactory.createLoan(...);
    loan = loanRepository.save(loan);
    bookRepository.save(book);
    publisher.publish(new BookLoanedEvent(...));
    return loan;
}
```

## 读路径：`LendingQueryService`

查询服务直接以仓储投影返回视图，不经过写模型聚合：

```java
// LendingQueryService：读模型
@Transactional(readOnly = true)
public Optional<BookStatusView> bookStatus(BookId bookId) {
    return bookRepository.findById(bookId)
        .map(book -> BookStatusView.of(
            book.getId(), book.getTitle(),
            book.availableCount(), book.borrowedCount(),
            reservationRepository.findByBookOrderedByCreatedAt(bookId)));
}

@Transactional(readOnly = true)
public List<Loan> overdueLoans(LocalDate today) {
    return loanRepository.findOverdueLoans(today);
}
```

读模型 `BookStatusView` 是独立的视图对象，组合了馆藏统计与预约队列，专为展示设计。

## 收益与取舍

- **收益**：读路径可针对查询优化（投影、索引、物化视图），不污染写模型；写路径保持领域纯度。
- **取舍**：CQRS 增加一层查询服务与视图类型。本示例为单体、单数据库，读走同一仓储的投影查询；真正的 CQRS 读模型（独立物化视图、事件投影）在写入侧异步构建，属于更重的方案，按需取舍。

## 小结

读与写分离是让领域模型专注"行为"、让查询更高效的手段。`LendingQueryService` 演示了轻量 CQRS 的落地方式。
