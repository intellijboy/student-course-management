# Design

## Context

- 见 `proposal.md - Why`。工程骨架已完成，现有唯一端点 `GET /demo`（build-infrastructure 的"空壳应用 HTTP 响应"需求所兑现，本次保持不变）。
- 技术栈：Java 21、Spring Boot 4.1.2（spring-boot-starter-web）、Kotlin DSL 构建、无数据库依赖。
- 本变更往 `build-infrastructure` capability 增加 Book CRUD 需求（specs 见 `specs/build-infrastructure/spec.md`）。

## Goals / Non-Goals

**Goals:**
- 提供一组可运行、可测试的 Book CRUD REST 端点，作为 Spring Boot REST 入门示例。
- 演示常见手法：路径参数、请求体绑定、JSON 序列化、状态码（200/201/204/404）。
- 只聚焦 REST 层接口设计与实现，保持零外部依赖（Controller 内嵌内存存储）。

**Non-Goals:**
- 引入 JPA、数据库、分页、校验框架（Bean Validation）。
- 统一响应包装、全局异常处理骨架（示例保持最小；RBAC 业务进场时再引入）。
- 认证鉴权、幂等/并发语义、跨域配置。
- 引入 domain / repository / service 分层设计——本变更只做 REST 层。

## Decisions

### D1. API 形状
```
GET    /api/books         -> 200 + Book[]         （列表）
GET    /api/books/{id}    -> 200 + Book / 404     （单个）
POST   /api/books         -> 201 + Book           （创建，服务端生成 id）
PUT    /api/books/{id}    -> 200 + Book / 404     （更新 title/author）
DELETE /api/books/{id}    -> 204 / 404            （删除）
```
- 选择 `ResponseEntity` 直接控制状态码，最小化依赖。
- **备选**：`@ResponseStatus` + 异常（`ResponseStatusException`）——更"框架化"但也更隐晦，示例阶段不用。

### D2. Book 表示与存储
- `Book` 使用 Java `record`（`Long id`、`String title`、`String author`），作为 REST 请求/响应 JSON 载体，贴合 Java 21 风格、序列化零配置。
- 内存存储内嵌在 `BookController` 内：`ConcurrentHashMap<Long, Book>` + `AtomicLong` 生成 id，保证示例并发安全且无需第三方库。
- **备选**：`ArrayList` 手动同步——示例能跑但不具备基本线程安全，不采用。

### D3. 代码组织（仅 REST 层）
```
cn.intellijboy.demo.web
  |-- Book.java           record (JSON 载体)
  `-- BookController.java REST 端点 (@RestController, 内嵌内存存储)
```
- 本变更只设计 REST 层：单一 Controller 直接持有内存存储并实现全部端点，不引入 domain / repository / service 分层。
- **备选**：拆出 Repository 接口与实现——可演示依赖注入，但超出"只考虑 REST 层"的边界，不采用。

### D4. 测试
- 沿用 `RbacOpenspecDemoApplicationTests` 的模式：`@SpringBootTest` + `MockMvcBuilders.webAppContextSetup`（Spring Boot 4 移除了 `@AutoConfigureMockMvc`）。
- 每个端点一个测试组：成功路径 + 404 路径。
- 使用独立测试数据（测试方法内自建数据），不依赖运行时状态。

## Risks / Trade-offs

- [内存存储无持久化：重启即丢失] → 作为入门示例这是预期行为；在 tasks 中明确标注，避免后续误认为"数据被保存"。
- [record 作为实体对 Jackson 反序列化要求 id 为 null/缺省] → 创建端点只接收 title/author，id 由服务端生成后返回，避免客户端传 id。
- [并发写 id 冲突] → `AtomicLong` 单调递增，示例规模下无冲突。
- [存储内嵌 Controller 使示例无法演示分层] → 这正是本次刻意取舍：聚焦 REST 层；后续 RBAC 业务进场时再演进分层。

## Migration Plan

绿地新增，无迁移、无回滚路径。

## Open Questions

无。
