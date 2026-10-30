# 08 示例工程导览：从教程到代码的快速上手

> 配套代码：`src/main/java/cn/intellijboy/demo/librarylending/`、`src/main/java/cn/intellijboy/demo/studentcourse/`（含 `src/test`）

## 工程结构速览

本工程包含两个并列的限界上下文，均按 `web → application → domain ← infrastructure` 组织：

```text
src/main/java/cn/intellijboy/demo/
├── librarylending/       图书借阅上下文（本系列核心示例）
│   ├── web/              REST 适配器（BookController / LoanController / ReservationController）
│   ├── application/      应用服务 + CQRS 读模型 + 组合根配置
│   ├── domain/           领域核心（model / service / event / repository / acl）
│   └── infrastructure/   JPA 实现 / 事件适配器 / 防腐层适配器
└── studentcourse/        学生课程上下文（分层示例，含领域事件与值对象示范）
    ├── web/  application/  domain/  infrastructure/
```

## 按教程阅读路径

| 教程篇 | 从哪看起 |
| --- | --- |
| 01 限界上下文与六边形 | `LibraryLendingDomainConfig`（组合根）、各层端口与实现 |
| 02 聚合/实体/值对象 | `domain/model/`：`Book`、`Loan`、`Reservation`、值对象 `record` |
| 03 领域服务与工厂 | `domain/service/`：`BorrowingPolicy`、`ReservationPromotionService`、`BookFactory` |
| 04 领域事件 | `domain/event/`：事件类 + `DomainEventPublisher` 端口；`SpringDomainEventPublisher` 适配器 |
| 05 防腐层 | `domain/acl/ReaderCatalogGateway` + `infrastructure/acl/ReaderCatalogGatewayAdapter` |
| 06 CQRS | `application/LendingQueryService` 与 `BookStatusView` 读模型 |
| 07 应用服务与事务 | `application/LoanApplicationService`（借阅/归还/预约用例） |

## 测试地图（测试优先阅读）

| 测试文件 | 验证内容 |
| --- | --- |
| `domain/model/BookTest`、`LoanTest`、`ReservationTest` | 聚合不变式与状态流转（纯领域，无 Spring） |
| `domain/service/BorrowingPolicyAndFactoryTest` | 领域服务与工厂 |
| `application/LoanApplicationServiceTest` | 借阅/归还/预约/转正/逾期/防腐层（内存 stub） |
| `application/LendingQueryServiceTest` | 读模型查询与统计 |
| `infrastructure/LendingRepositoryIntegrationTest` | JPA 读写与事件发布 |
| `web/LibraryLendingControllerTest` | REST 接口（MockMvc） |
| `application/studentcourse/GradeEventPublishTest` | studentcourse 成绩事件发布示范 |

## 运行与验证

```bash
# 运行全部测试（110+ 项，覆盖领域/应用/基础设施/接口）
./gradlew test

# 启动应用并访问图书借阅接口
./gradlew bootRun
# POST /api/books          登记图书（title/author/copyCount）
# POST /api/loans          借阅（readerId/bookId，readerId 复用学生）
# POST /api/reservations   预约（全部副本借出时）
# POST /api/loans/{id}/return  归还（含预约转正与逾期费用）
# GET  /api/books/{id}     馆藏状态与预约队列
# GET  /api/readers/{id}/loans  读者借阅列表
# GET  /api/loans/overdue  逾期列表
```

## 一句话导览

先读 01 理解边界与依赖方向 → 02-04 掌握领域建模三件套（聚合、值对象、领域事件）→ 05 看上下文如何协作 → 06-07 看应用层如何落地用例 → 再用测试地图逐层验证理解。
