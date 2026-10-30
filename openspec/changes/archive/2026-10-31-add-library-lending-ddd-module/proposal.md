# Proposal

## Why

现有 `studentcourse` 模块虽然按 web/application/domain/infrastructure 分层，但领域模型偏向贫血模型（实体多为 getter/setter），未落地聚合根、值对象、领域服务、领域事件、防腐层等 DDD 核心实践，不足以作为"DDD 最佳实践示例"。需要一个与 `studentcourse` 并列、完整覆盖 DDD 核心思想与最佳实践的教学模块，并对现有模块顺带做对齐重构；同时配套产出 DDD 系列博客教程（随示例工程存放于代码仓库 `docs/`），让读者通过阅读教程快速理解示例工程。

## What Changes

- **新增 `library-lending`（图书借阅）模块**：与 `studentcourse` 并列的完整 DDD 示例，覆盖书目馆藏、借阅、归还、逾期规则与预约借阅。模块完整落地以下 DDD 核心实践：
  - 限界上下文与模块边界（独立包 + 显式依赖规则）
  - 聚合根（`Book` 馆藏、`Loan` 借阅记录、`Reservation` 预约）、实体、值对象
  - 领域服务（借阅策略、逾期费用计算、预约转正编排）与工厂
  - 领域事件（借出、归还、预约创建、预约转正、逾期）与事件发布
  - 仓储端口（领域层）与 JPA 实现（基础设施层）
  - 防腐层（ACL）：复用现有 `studentcourse` 上下文的学生作为读者
  - 六边形架构的端口与适配器（inbound 端口 + REST 适配器，outbound 端口 + 仓储/事件适配器）
  - 应用服务编排与事务边界
  - CQRS 查询分离（读模型）
  - 模块间通信（领域事件驱动状态流转与预约转正）
- **顺带重构现有 `studentcourse` 模块**：贫血模型 → 充血模型（行为内聚），补齐值对象与领域事件等基础实践；**行为保持不变**（学生/课程/成绩 CRUD 与查询语义不变），仅代码结构对齐。
- **新增 DDD 系列博客教程**：写入代码仓库 `docs/ddd-tutorials/`，覆盖限界上下文、聚合根与值对象、领域服务、领域事件、防腐层、六边形架构、CQRS 及示例工程导览，使读者能通过教程快速了解本示例工程。
- **测试重点**：领域层单元测试（聚合/值对象/领域服务）> 应用层 > 基础设施层 > 少量接口层。
- **技术形态**：后端 + 事件机制。领域事件基于 Spring `ApplicationEventPublisher` 发布（无新第三方依赖）；不新增前端页面。

## Capabilities

### New Capabilities

- `library-lending`: 图书借阅管理能力——书目与馆藏、借阅登记、归还、逾期规则、预约借阅、借阅查询与统计，以及与现有学生上下文（读者）的防腐层对接。

### Modified Capabilities

<!-- 现有 student-course-management 行为保持不变：本次对其仅为代码结构重构（贫血→充血模型），不改变任何需求语义，因此不生成 delta spec。 -->
无

## Impact

- **代码**：新增 `cn.intellijboy.demo.{web,application,domain,infrastructure}.librarylending` 包结构（与 `studentcourse` 并列）；重构现有 `studentcourse` 包内领域模型与相关层；新增防腐层适配器（复用现有学生 JPA 仓储）。
- **文档**：新增 `docs/ddd-tutorials/` DDD 系列博客教程。
- **测试**：新增 `src/test` 下 librarylending 模块的领域层/应用层/基础设施层/接口层测试；补充现有 studentcourse 模块重构后的回归测试。
- **依赖**：无新增第三方依赖（事件机制复用 Spring 内置机制）。
- **行为**：`studentcourse` 现有 REST API 与页面行为不变；新增 librarylending 模块的 REST API（借阅相关端点）。
- **规格**：新增 `library-lending` capability spec；`student-course-management` spec 不变。
