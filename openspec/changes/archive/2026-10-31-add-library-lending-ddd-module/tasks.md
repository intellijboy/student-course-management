# Tasks

## 1. 领域层：图书借阅模块核心建模

- [x] 1.1 创建 `cn.intellijboy.demo.librarylending.domain` 包结构（model/service/event/repository/acl）并实现值对象（`ReaderId`、`BookId`、`BookCopyId`、`LoanPeriod`、`OverdueFee`）——验证：值对象不变式单元测试通过（借期非负、费率计算正确）
- [x] 1.2 实现 `Book` 聚合根与 `BookCopy` 实体（馆藏副本 AVAILABLE/BORROWED/RESERVED 状态、馆藏数量与可借统计）——验证：领域单元测试覆盖馆藏登记与状态流转
- [x] 1.3 实现 `Loan` 聚合根（借阅记录、同一读者同一副本唯一不变式、应还期限与逾期判定）——验证：领域单元测试覆盖重复借阅拒绝与逾期判定
- [x] 1.4 实现 `Reservation` 聚合根（预约队列按创建顺序、WAITING/NOTIFIED 状态流转）——验证：领域单元测试覆盖预约排队与转正
- [x] 1.5 实现领域服务 `BorrowingPolicy`（可借判断、逾期费率）与 `BookFactory`/`LoanFactory`（创建校验）——验证：领域单元测试覆盖策略与工厂校验
- [x] 1.6 定义领域事件（`BookLoanedEvent`、`BookReturnedEvent`、`ReservationCreatedEvent`、`ReservationPromotedEvent`、`LoanOverdueEvent`）与 `DomainEventPublisher` 端口——验证：编译通过且事件/端口单测可用

## 2. 防腐层与应用层

- [x] 2.1 定义防腐层端口 `ReaderCatalogGateway` 并实现适配器（复用现有 `studentcourse` 的 `JpaStudentRepository`，翻译为 `ReaderRef`，不引用对方领域模型）——验证：应用层测试验证读者存在与不存在拒绝
- [x] 2.2 实现 `CatalogApplicationService`（登记图书与馆藏副本、按图书查询馆藏）——验证：应用层测试覆盖馆藏登记与查询
- [x] 2.3 实现 `LoanApplicationService`（借阅、归还，事务边界内编排 `Book`/`Loan`/`Reservation`，归还触发预约转正）——验证：应用层测试覆盖借阅/归还/重复借阅拒绝/预约转正/逾期费用
- [x] 2.4 实现 `LendingQueryService`（CQRS 读模型：按读者查借阅、按图书查馆藏与预约队列、逾期列表）——验证：应用层测试验证读模型返回与统计正确

## 3. 基础设施层与接口层

- [x] 3.1 实现 JPA 持久化（`Book`/`Loan`/`Reservation` 实体与 Spring Data 仓储）及 `DomainEventPublisher` 的 Spring `ApplicationEventPublisher` 适配器——验证：基础设施层集成测试验证读写与事件发布
- [x] 3.2 实现 REST 接口层（`web.librarylending` controller 与 DTO，端点如 `/api/books`、`/api/loans`、`/api/reservations` 等）——验证：接口层 MockMvc 测试覆盖主要端点（少量接口测试）

## 4. 现有 studentcourse 模块重构（行为不变）

- [x] 4.1 领域模型充血化：将行为/校验收敛为实体方法，收敛可变 setter 暴露；引入 `Email`、`Score` 值对象——验证：现有学生/课程/成绩接口测试回归通过
- [x] 4.2 补充基础领域事件（成绩登记/修改发布事件，仅作示范，不引入跨模块消费）——验证：相关单元测试通过
- [x] 4.3 运行全部测试回归，确认学生/课程/成绩 REST 行为与页面不变——验证：`./gradlew test` 全绿，接口语义无变化

## 5. DDD 系列博客教程撰写（代码仓库 docs/）

- [x] 5.1 撰写教程索引与第 01 篇（限界上下文与分层/六边形架构）至 `docs/ddd-tutorials/`，结合 `librarylending` 包结构讲解——验证：文档命令/结构可读，链接有效
- [x] 5.2 撰写第 02-03 篇（聚合根/实体/值对象、领域服务与工厂）至 `docs/ddd-tutorials/`——验证：文档结构与代码一致
- [x] 5.3 撰写第 04-05 篇（领域事件与事件驱动、防腐层与上下文集成）至 `docs/ddd-tutorials/`——验证：文档结构与代码一致
- [x] 5.4 撰写第 06-07 篇（CQRS 查询分离、应用服务与事务边界）至 `docs/ddd-tutorials/`——验证：文档结构与代码一致
- [x] 5.5 撰写第 08 篇（示例工程导览）并核对全部教程与实现代码一致——验证：读者按索引可通读全部教程并对照示例工程

## 6. 集成验证

- [x] 6.1 端到端验证图书借阅全流程（登记馆藏→借阅→满借拒绝→归还→预约转正→逾期费用→防腐层读者校验），覆盖 spec 全部场景——验证：启动应用后通过 REST 或集成测试逐场景验证 HTTP 与状态正确

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更并同步 `library-lending` 主 spec。
- 归档后运行 openspec validate 校验主 spec 合并结果。
