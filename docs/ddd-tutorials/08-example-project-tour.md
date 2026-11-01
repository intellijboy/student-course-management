# 08 示例工程导览：从教程到代码的快速上手

> 配套代码：`sc-domain` → `cn.intellijboy.domain.librarylending/` 与 `cn.intellijboy.domain.studentcourse/`，应用/基础设施/接口分别位于 `sc-application`、`sc-infrastructure`、`sc-interfaces` 对应包（含各模块 `src/test`）。

## 工程结构速览

本工程按 DDD 分层组织为 `sc-` 前缀的多模块 Gradle 工程，两个并列的限界上下文（`librarylending`、`studentcourse`）各自横跨各层模块。依赖规则：`interfaces → application → domain ← infrastructure`，领域层不依赖框架。

```text
sc-common/          共享工具类（cn.intellijboy.common，如 StringUtils）
sc-domain/          领域核心（无框架依赖，按上下文分包）
│   cn.intellijboy.domain.librarylending/   图书借阅上下文（本系列核心示例）
│       model/  service/  event/  repository/  acl/   exception/
│   cn.intellijboy.domain.studentcourse/     学生课程上下文
sc-application/     应用服务 + CQRS 读模型 + 组合根配置
│   cn.intellijboy.application.librarylending/   LoanApplicationService / LendingQueryService / BookStatusView / LibraryLendingDomainConfig
│   cn.intellijboy.application.studentcourse/
sc-infrastructure/  JPA 实现 / 事件适配器 / 防腐层适配器
│   cn.intellijboy.infrastructure.librarylending/  persistence / mapper / repository / event / acl
│   cn.intellijboy.infrastructure.studentcourse/
sc-interfaces/      REST 适配器（原 web 层）
│   cn.intellijboy.interfaces.librarylending/  book/ loan/ reservation/（Controller + DTO）
│   cn.intellijboy.interfaces.studentcourse/
sc-main/            装配模块：Spring Boot 启动类 cn.intellijboy.StudentCourseManagementApplication
```

## 按教程阅读路径

| 教程篇 | 从哪看起 |
| --- | --- |
| 01 限界上下文与六边形 | `LibraryLendingDomainConfig`（sc-application 组合根）、各层端口与实现 |
| 02 聚合/实体/值对象 | `sc-domain` 的 `domain/librarylending/model/`：`Book`、`Loan`、`Reservation`、值对象 `record` |
| 03 领域服务与工厂 | `sc-domain` 的 `domain/librarylending/service/`：`BorrowingPolicy`、`ReservationPromotionService`、`BookFactory` |
| 04 领域事件 | `sc-domain` 的 `domain/librarylending/event/`：事件类 + `DomainEventPublisher` 端口；`sc-infrastructure` 的 `SpringDomainEventPublisher` 适配器 |
| 05 防腐层 | `sc-domain` 的 `domain/librarylending/acl/ReaderCatalogGateway` + `sc-infrastructure` 的 `infrastructure/librarylending/acl/ReaderCatalogGatewayAdapter` |
| 06 CQRS | `sc-application` 的 `application/librarylending/LendingQueryService` 与 `BookStatusView` 读模型 |
| 07 应用服务与事务 | `sc-application` 的 `application/librarylending/LoanApplicationService`（借阅/归还/预约用例） |

## 测试地图（测试优先阅读）

| 测试文件 | 验证内容 |
| --- | --- |
| `sc-domain/src/test/.../domain/librarylending/model/BookTest`、`LoanTest`、`ReservationTest` | 聚合不变式与状态流转（纯领域，无 Spring） |
| `sc-domain/src/test/.../domain/librarylending/service/BorrowingPolicyAndFactoryTest` | 领域服务与工厂 |
| `sc-application/src/test/.../application/librarylending/LoanApplicationServiceTest` | 借阅/归还/预约/转正/逾期/防腐层（内存 stub） |
| `sc-application/src/test/.../application/librarylending/LendingQueryServiceTest` | 读模型查询与统计 |
| `sc-main/src/test/.../infrastructure/librarylending/LendingRepositoryIntegrationTest` | JPA 读写与事件发布 |
| `sc-main/src/test/.../interfaces/librarylending/LibraryLendingControllerTest` | REST 接口（MockMvc） |
| `sc-main/src/test/.../application/studentcourse/GradeEventPublishTest` | studentcourse 成绩事件发布示范 |

## 运行与验证

```bash
# 运行全部测试（多模块根目录执行，覆盖领域/应用/基础设施/接口，110+ 项）
./gradlew test

# 启动应用并访问图书借阅接口（装配模块 sc-main）
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