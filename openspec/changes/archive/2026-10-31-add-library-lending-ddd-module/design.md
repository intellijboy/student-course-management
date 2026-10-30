# Design

## Context

现有工程为 Spring Boot 4.1.2 + Java 21 + Gradle + H2/JPA 单体。现有 `studentcourse` 模块已按 web/application/domain/infrastructure 分层，但领域模型偏贫血（实体多为 getter/setter），未落地聚合根、值对象、领域服务、领域事件、防腐层等核心实践。动机与范围见 proposal.md - Why/What Changes。本设计为新增 `library-lending` 模块、顺带重构 `studentcourse` 模块，并规划配套 DDD 系列博客教程定义技术方案，规格见 specs/library-lending/spec.md。

## Goals / Non-Goals

**Goals:**

- 新增 `library-lending` 模块，完整演示 DDD 核心实践：限界上下文与依赖规则、聚合根、值对象、实体、领域服务、领域事件、仓储端口、工厂、防腐层、六边形端口-适配器、应用服务编排、CQRS 读模型、事件驱动的状态流转。
- 顺带重构现有 `studentcourse` 模块：贫血模型 → 充血模型，补齐值对象与基础领域事件；**行为保持不变**。
- 产出 DDD 系列博客教程（`docs/ddd-tutorials/`），使读者能通过教程快速理解示例工程。
- 测试重点：领域层单元测试 > 应用层 > 基础设施层 > 少量接口层。

**Non-Goals:**

- 不新增前端页面。
- 不引入事件溯源持久化，不引入独立消息中间件（Kafka/RabbitMQ）；事件采用单体内同步发布演示。
- 不拆分微服务/独立部署；不改动 `student-course-management` 的 spec 与对外行为。
- 不新增第三方依赖。

## Decisions

### D1. 模块边界与包结构（限界上下文）

新增 `cn.intellijboy.demo.librarylending` 顶层包，与 `studentcourse` 并列，按六边形端口-适配器组织：

```text
librarylending
├── web/            inbound 适配器：REST controller、DTO
├── application/    应用服务（用例编排、事务边界）+ CQRS 查询服务
├── domain/         核心（无框架依赖）
│   ├── model/      聚合根 Book、Loan、Reservation；值对象；实体
│   ├── service/    领域服务（借阅策略、逾期费用计算、预约转正编排）
│   ├── event/      领域事件与 DomainEventPublisher 端口
│   ├── repository/ 仓储端口
│   └── acl/        防腐层端口（读者目录网关）
└── infrastructure/ outbound 适配器：JPA 仓储实现、防腐层适配器、事件发布实现
```

依赖规则：`web → application → domain ← infrastructure`；`domain` 定义全部端口接口；`infrastructure` 实现端口；禁止跨层调用与反向依赖。

现有 `studentcourse` 模块保持现有包路径（`cn.intellijboy.demo.{web,application,domain,infrastructure}.studentcourse`），仅内部对齐 DDD 规范，不做大规模包移动。

### D2. 图书借阅领域建模（聚合根与不变式）

- **`Book`（聚合根，书目与馆藏）**：`title`、`author`、馆藏副本集合（`BookCopy` 实体，`AVAILABLE`/`BORROWED`/`RESERVED` 状态）。聚合方法维护馆藏不变式。
- **`Loan`（聚合根，借阅记录）**：`readerId`、`bookCopyId`、借出时间、应还期限、归还时间、逾期费用。不变式：同一读者同一馆藏副本至多一条在借记录。
- **`Reservation`（聚合根，预约）**：`readerId`、`bookId`、`status`（WAITING / NOTIFIED / FULFILLED / CANCELLED）、创建时间；预约按创建时间排序。
- **值对象**：`ReaderId`（防腐层翻译的学生引用）、`BookId`、`BookCopyId`、`LoanPeriod`（借期）、`OverdueFee`（逾期费用，含费率计算）。
- **领域服务**：`BorrowingPolicy`（借阅策略：副本可借判断、逾期费率）；`ReservationPromotionService`（归还后按预约顺序转正编排）。策略与编排拆分为领域服务，便于单元测试。
- **领域事件**：`BookLoanedEvent`、`BookReturnedEvent`、`ReservationCreatedEvent`、`ReservationPromotedEvent`、`LoanOverdueEvent`。
- **工厂**：`BookFactory`（创建书目与馆藏副本）、`LoanFactory`（创建借阅记录时校验重复借阅）。

**备选与取舍**：将预约与借阅合并进 `Book` 单一聚合——更简单但无法演示"多聚合 + 跨聚合协调"与聚合边界；采用 `Book`/`Loan`/`Reservation` 三聚合体现聚合边界与事务编排，权衡见 Risks。

### D3. 防腐层（ACL）与数据来源

- **端口（domain）**：`ReaderCatalogGateway`（校验读者存在性并提供读者引用）。
- **适配器（infrastructure）**：复用现有 `studentcourse` 的 `JpaStudentRepository`，将对方 `Student` 翻译为本上下文需要的 `ReaderRef`（读者 id/姓名），不直接引用对方领域模型——体现上下文隔离。
- **馆藏数据归属**：书目、馆藏副本、借期规则等全部属于 `library-lending` 上下文，由本地聚合维护；防腐层只提供读者主数据（存在性、姓名）。

### D4. CQRS 查询分离

- 写路径：`LoanApplicationService`/`CatalogApplicationService` 编排聚合与事务。
- 读路径：`LendingQueryService` 直接以投影/JPA 查询返回读模型 DTO（读者借阅列表、图书馆藏与预约队列、逾期列表），不经过写模型聚合，演示命令与查询分离。

### D5. 事件发布机制

- `DomainEvent` 基类 + `DomainEventPublisher` 端口（domain）→ infrastructure 基于 Spring `ApplicationEventPublisher` 同步发布（单体演示，零第三方依赖）。
- 事件用于模块内状态流转（如归还后触发预约转正）与后续可观测性扩展；预留切换消息中间件的适配点。

### D6. 六边形端口-适配器落地

- inbound 端口：应用服务接口（`LoanApplicationService`、`CatalogApplicationService`），REST controller 为其适配器。
- outbound 端口：仓储接口、`DomainEventPublisher`、防腐层网关（domain 定义，infrastructure 实现）。

### D7. 现有 studentcourse 模块重构方案

- 领域模型充血化：将校验与行为收敛为实体方法（如 `Student.updateName/updateEmail` 校验、`CourseGrade` 已有分数行为），收敛可变 setter 暴露。
- 补值对象：`Email`（格式校验）、`Score`（0-100 不变式，替代裸 Integer）。
- 补基础领域事件：成绩登记/修改发布事件（仅作为基础示范，不引入跨模块消费）。
- 明确约束：**REST API 语义与页面行为不变**，以现有接口测试回归保障。

### D8. DDD 系列博客教程结构（`docs/ddd-tutorials/`）

教程随示例工程存放于代码仓库 `docs/ddd-tutorials/`，与实现并行撰写，每篇结合本工程的 `library-lending`/`studentcourse` 代码讲解：

| 篇 | 主题 | 结合示例 |
| --- | --- | --- |
| 01 | 限界上下文与分层/六边形架构 | `librarylending` 包结构与依赖规则 |
| 02 | 聚合根、实体与值对象 | `Book`/`Loan`/`Reservation` 与值对象不变式 |
| 03 | 领域服务与工厂 | `BorrowingPolicy`、`BookFactory` 等 |
| 04 | 领域事件与事件驱动 | 归还触发预约转正的事件流 |
| 05 | 防腐层（ACL）与上下文集成 | `ReaderCatalogGateway` 对接 `studentcourse` |
| 06 | CQRS 查询分离 | `LendingQueryService` 读模型 |
| 07 | 应用服务与事务边界 | 借阅/归还用例编排 |
| 08 | 示例工程导览 | 从教程到代码的快速上手路径 |

教程目录以独立 Markdown 文件产出，索引见 `docs/ddd-tutorials/README.md`。

## Risks / Trade-offs

- [跨聚合一致性：借阅/归还/预约转正涉及 `Book`/`Loan`/`Reservation` 多聚合] → 单体应用内由应用服务在同一事务边界内编排聚合，保证强一致；在文档中说明生产环境 DDD 常用"领域事件 + 最终一致性"的备选，本示例为教学清晰选择事务编排。
- [馆藏副本与借阅状态的并发竞争] → 聚合内以事务内读取-校验-更新保证不变式；单元测试覆盖可借判断、重复借阅、预约转正边界。
- [现有 studentcourse 重构引入回归] → 约束"行为不变"，依赖现有接口测试回归；重构与新增模块分阶段落地。
- [范围较大（新增模块 + 重构 + 教程 + 分层测试）] → tasks 按阶段拆分（先新增模块、后重构现有模块、教程并行、最后集成验证），每阶段可独立验证。
- [内存同步事件总线的局限] → 明确单体内演示定位；`DomainEventPublisher` 端口预留替换为消息中间件的适配点。

## Migration Plan

- 无数据迁移：新增 `librarylending` 模块新增独立表结构（H2 `create-drop` 重建）；`studentcourse` 重构保持既有表结构不变。
- 回滚策略：删除 `librarylending` 包与相关测试即可回滚新增模块；`studentcourse` 重构若需回滚，恢复原模型文件即可（行为不变，风险低）。

## Open Questions

- 无。馆藏/借期规则归属 `library-lending` 上下文已明确；现有 `student-course-management` 行为不变已明确，不生成 delta spec；教程存放位置（代码仓库 `docs/ddd-tutorials/`）已确认。
