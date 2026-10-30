# 05 防腐层与上下文集成

> 配套代码：`cn.intellijboy.demo.librarylending.domain.acl`、`...infrastructure.acl`

## 为什么需要防腐层

两个限界上下文各自的领域模型不同。`librarylending` 需要"读者"，但读者数据在 `studentcourse` 上下文里以 `Student` 模型存在。若直接引用对方模型，会让两个上下文耦合、术语污染。

防腐层（Anti-Corruption Layer, ACL）在中间做**翻译**：对方上下文的数据进入本上下文时，被转换为本上下文自己的模型。

## 端口与适配器

`ReaderCatalogGateway` 是领域层端口，返回本上下文的 `ReaderRef`：

```java
// domain/acl
public record ReaderRef(ReaderId readerId, String name) { ... }

public interface ReaderCatalogGateway {
    Optional<ReaderRef> findReader(ReaderId readerId);
}
```

适配器复用现有 `studentcourse` 的仓储，把 `Student` 翻译为 `ReaderRef`，**不把对方领域模型泄漏进本上下文**：

```java
// infrastructure/acl
@Component
public class ReaderCatalogGatewayAdapter implements ReaderCatalogGateway {

    private final JpaStudentRepository studentRepository; // 复用现有仓储

    @Override
    public Optional<ReaderRef> findReader(ReaderId readerId) {
        return studentRepository.findById(readerId.value())
            .map(student -> new ReaderRef(readerId, student.getName()));
    }
}
```

## 使用防腐层：借阅前校验读者

应用服务通过端口使用防腐层，读者不存在即拒绝：

```java
// LoanApplicationService
@Transactional
public Loan borrow(ReaderId readerId, BookId bookId) {
    requireReader(readerId);                    // 防腐层校验
    Book book = requireBook(bookId);
    if (!policy.hasAvailableCopy(book)) {
        throw new BookCopyUnavailableException("该图书所有副本均被借出，可预约");
    }
    // ...
}

private ReaderId requireReader(ReaderId readerId) {
    readerGateway.findReader(readerId)
        .orElseThrow(() -> new ReaderNotFoundException("读者不存在: id=" + readerId.value()));
    return readerId;
}
```

## 设计要点

- 防腐层**只暴露本上下文需要的最小信息**（如读者 id、姓名），不暴露对方聚合的全部能力。
- 对方上下文内部变化（如 `Student` 增加字段）不会波及其它模块的领域层。
- 若将来改为微服务，适配器换成 REST 客户端即可，领域层与用例层不变。

## 小结

防腐层是上下文集成时的"翻译层"：端口定义在领域层，适配器在基础设施层复用对方仓储或 API，保证上下文隔离与依赖方向。
