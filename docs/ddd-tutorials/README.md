# DDD 示例代码系列教程

本系列教程配套本工程的 DDD 示例代码（`librarylending` 图书借阅模块与 `studentcourse` 学生课程模块），以**代码讲解为主**，帮助读者通过阅读教程快速理解示例工程并掌握 DDD 核心实践。

## 目录

| 篇 | 主题 | 对应代码 |
| --- | --- | --- |
| [01 限界上下文与分层/六边形架构](./01-bounded-context-and-hexagonal-architecture.md) | 模块边界、依赖规则、端口与适配器 | `librarylending` 包结构 |
| [02 聚合根、实体与值对象](./02-aggregates-entities-value-objects.md) | 聚合边界、实体与不可变值对象 | `Book`/`Loan`/`Reservation`、`BookId`/`OverdueFee` 等 |
| [03 领域服务与工厂](./03-domain-services-and-factories.md) | 领域服务与创建逻辑收敛 | `BorrowingPolicy`、`BookFactory`/`LoanFactory` |
| [04 领域事件与事件驱动](./04-domain-events.md) | 事件建模与发布、状态流转 | 领域事件 + `DomainEventPublisher` 端口 |
| [05 防腐层与上下文集成](./05-anti-corruption-layer.md) | 上下文隔离、翻译与复用 | `ReaderCatalogGateway` 对接 `studentcourse` |
| [06 CQRS 查询分离](./06-cqrs-query-separation.md) | 读模型与写模型分离 | `LendingQueryService` 读模型 |
| [07 应用服务与事务边界](./07-application-services-and-transactions.md) | 用例编排与事务 | `LoanApplicationService`、`CatalogApplicationService` |
| [08 示例工程导览](./08-example-project-tour.md) | 从教程到代码的快速上手 | 全部模块 |

## 前置阅读

- 每个模块按 `web → application → domain ← infrastructure` 分层，领域层不依赖框架。
- 运行 `./gradlew test` 可执行全部示例测试；`./gradlew bootRun` 可启动应用访问 `/api/books`、`/api/loans` 等接口。
