# Proposal

## Why

工程已有构建骨架与 REST 入门示例（Book CRUD），需要一份体现经典 DDD 分层 + 真实持久化的标准业务示例，作为后续业务开发的架构范式参照。

## What Changes

- 新增"学生课程管理"业务模块，领域对象：学生（Student）、课程（Course）、课程成绩（CourseGrade）。
- 采用经典 DDD 分层（interface / application / domain / infrastructure），领域模型为纯 POJO，持久化通过基础设施层实现（方案 B）。
- 持久化采用 H2 内嵌数据库 + Spring Data JPA，领域模型与 JPA 持久化模型分离映射。
- 提供 REST API：学生/课程标准 CRUD，成绩登记/修改/删除、按学生查询成绩、按课程查询成绩。
- 业务规则：
  - 成绩范围 0-100，越界时域层拒绝。
  - 同一学生同一课程仅有一条成绩，重复登记视为非法。
- 提供浏览器版 Vue3 + Tailwind 单页前端（本地静态资源托管），覆盖全部功能页面，与 REST API 并存。
- 现有 Book 示例与 build-infrastructure capability 保持不变。

## Capabilities

### New Capabilities
- `student-course-management`: 覆盖学生、课程、课程成绩的管理能力——REST API 与前端页面，含成绩范围与唯一性领域规则。

### Modified Capabilities
<!-- 无既有 capability 变更 -->

## Impact

- **代码**：新增 DDD 四层包结构（`domain` / `application` / `infrastructure` / `web`）下的学生课程管理实现；`src/main/resources/static` 下新增 Vue3 + Tailwind 前端静态资源。
- **依赖**：新增 `spring-boot-starter-data-jpa`、`com.h2database:h2`、`spring-boot-starter-web`（已有）。
- **配置**：`application.yml` 新增 H2 数据源与 JPA 配置。
- **API**：新增 `/api/students`、`/api/courses`、`/api/grades` 一组 REST 端点；新增前端入口页面。
- **系统**：`openspec/specs/student-course-management/spec.md` 在归档时创建。
