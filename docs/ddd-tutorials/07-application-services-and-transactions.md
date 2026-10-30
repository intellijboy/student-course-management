# 07 应用服务与事务边界

> 配套代码：`cn.intellijboy.demo.librarylending.application`

## 应用服务的职责

应用服务（Application Service）是**用例编排**层：协调多个聚合/仓储/端口完成一个用例，并管理事务边界。它不承载业务规则（业务规则在领域层），只负责"把领域能力组装成用例"。

本工程的应用服务：

| 应用服务 | 用例 |
| --- | --- |
| `CatalogApplicationService` | 登记图书与馆藏副本、查询馆藏 |
| `LoanApplicationService` | 借阅、归还、预约创建 |
| `LendingQueryService` | 读模型查询（CQRS，见第 06 篇） |

## 事务边界：跨聚合的一致性

归还用例同时影响 `Loan`、`Book`（副本状态）、`Reservation`（预约转正）三个聚合。单体应用通过 `@Transactional` 把整个用例放进一个事务，保证强一致：

```java
@Transactional
public ReturnResult returnCopy(Long loanId, LocalDate today) {
    Loan loan = loanRepository.findById(loanId).orElseThrow(...);
    loan.returnBook(today);                       // 1. 归还借阅记录
    loanRepository.save(loan);

    Book book = requireBook(loan.getBookId());
    BookCopy copy = book.findCopy(loan.getBookCopyId()).orElseThrow(...);
    Optional<Reservation> promoted = reservationPromotionService.promoteNextFor(bookId); // 2. 预约转正
    if (promoted.isPresent()) {
        copy.returnToReserved();                  // 3. 副本归还给预约者
    } else {
        copy.returnCopy();
    }
    bookRepository.save(book);
    // 发布事件...
    return new ReturnResult(overdueFeeAmount, promoted.map(Reservation::getReaderId).orElse(null));
}
```

## 事务与 DDD 的权衡

- **事务编排 vs 最终一致性**：本示例为教学清晰，选择在应用服务内用单事务编排多聚合。生产环境 DDD 常采用"聚合即事务边界 + 领域事件 + 最终一致性"来避免长事务。示例在 `design.md` 中说明了这一权衡。
- **读操作**：查询服务使用 `@Transactional(readOnly = true)`，明确只读语义。

## 接口适配器

REST Controller（`web` 包）是应用服务的入站适配器：接收请求 DTO，转换为领域对象（`ReaderId`/`BookId`），调用应用服务，再把结果转回响应 DTO。领域异常由 `LibraryLendingExceptionHandler` 统一映射为 HTTP 状态。

## 小结

应用服务把"用例"落地：编排聚合与端口、界定事务、转换输入输出。业务规则保持在领域层，应用层只做组织与事务控制。
