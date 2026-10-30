# 01 限界上下文与分层/六边形架构

> 配套代码：`src/main/java/cn/intellijboy/demo/librarylending/`

## 什么是限界上下文

限界上下文（Bounded Context）是 DDD 对"模型边界"的界定：不同的业务子域拥有各自独立的领域模型，术语与规则只在各自上下文内有意义。本工程有两个并列的上下文：

- `studentcourse`：学生、课程、成绩管理。
- `librarylending`：图书借阅（书目、借阅、预约、逾期）。

两个上下文**互不引用对方的领域模型**。图书借阅需要"读者"时，通过防腐层把学生翻译为自己的 `ReaderId`（见第 05 篇）。

## 分层与依赖规则

以 `librarylending` 为例，包结构体现依赖方向：

```text
librarylending
├── web/            inbound 适配器：REST controller、DTO
├── application/    应用服务（用例编排、事务边界）+ CQRS 查询服务
├── domain/         核心（无框架依赖）
│   ├── model/      聚合根、实体、值对象
│   ├── service/    领域服务
│   ├── event/      领域事件与发布端口
│   ├── repository/ 仓储端口
│   └── acl/        防腐层端口
└── infrastructure/ outbound 适配器：JPA 实现、事件适配器、防腐层适配器
```

依赖规则：`web → application → domain ← infrastructure`。

- `domain` 只定义**接口（端口）**：`BookRepository`、`LoanRepository`、`DomainEventPublisher`、`ReaderCatalogGateway`。
- `infrastructure` 实现端口：`JpaBookRepository`、`SpringDomainEventPublisher`、`ReaderCatalogGatewayAdapter`。
- 领域层不依赖 Spring 注解（`@Service`、`@Component` 只出现在外层），保证领域逻辑可独立单元测试。

```java
// domain 定义端口
public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(BookId id);
    List<Book> findAll();
}

// infrastructure 实现端口（JPA）
@Repository
public class JpaBookRepository implements BookRepository { ... }
```

## 六边形架构：端口与适配器

六边形架构把应用划分为"内部（领域+应用）"与"外部（接口、持久化、外部系统）"：

- **入站端口**：应用服务接口（如 `LoanApplicationService`），REST Controller 是其适配器。
- **出站端口**：仓储接口、事件发布端口、防腐层网关，JPA/Spring/防腐层适配器是其实现。

核心收益：替换技术实现（例如 JPA 换 MyBatis、Spring 事件换 Kafka）不影响领域与用例层——只需替换对应适配器。

## 组合根

`LibraryLendingDomainConfig`（application 包）负责把纯领域服务（`BorrowingPolicy`、`BookFactory`、`LoanFactory`、`ReservationPromotionService`）注册为 Spring bean，保持领域层无框架依赖。

## 小结

- 上下文边界清晰、依赖单向、领域无框架依赖，是 DDD 代码组织的第一步。
- 端口定义在领域层、实现在外层，是依赖倒置的具体体现。
