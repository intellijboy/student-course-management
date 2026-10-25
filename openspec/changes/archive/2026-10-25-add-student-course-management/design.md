# Design

## Context

- 见 `proposal.md - Why`。工程已有构建骨架与 Book REST 示例；本变更引入经典 DDD 分层 + H2/JPA 持久化的标准业务示例。
- 技术栈：Java 21、Spring Boot 4.1.2、Kotlin DSL 构建、阿里云镜像。
- 需求见 `specs/student-course-management/spec.md`。

## Goals / Non-Goals

**Goals:**
- 落地经典 DDD 四层结构（interface / application / domain / infrastructure），领域模型为纯 POJO。
- 领域模型与 JPA 持久化模型分离，通过基础设施层实现映射。
- 完整覆盖学生/课程/成绩的 REST API 与浏览器管理页面。
- 领域规则（成绩 0-100、学生-课程唯一成绩）由 domain 层保证。

**Non-Goals:**
- 引入 MyBatis / 其他 ORM。
- 前端构建工具链（Vite/npm）——使用浏览器版 Vue3 + Tailwind 本地静态资源，无构建步骤。
- 认证鉴权、分页、校验框架（Bean Validation）、统一异常响应骨架。
- 生产级部署（本次为本地开发模式）。

## Decisions

### D1. 分层结构（DDD 方案 B）
```
cn.intellijboy.demo
  |-- domain/student-course
  |   |-- model/Student.java          领域实体(纯POJO)
  |   |-- model/Course.java
  |   |-- model/CourseGrade.java
  |   |-- repository/StudentRepository.java      仓库接口
  |   |-- repository/CourseRepository.java
  |   |-- repository/CourseGradeRepository.java
  |   `-- exception/...               领域异常(如成绩越界/重复)
  |-- application/student-course
  |   |-- StudentApplicationService.java   用例编排 + @Transactional
  |   |-- CourseApplicationService.java
  |   `-- CourseGradeApplicationService.java
  |-- infrastructure/student-course
  |   |-- persistence/StudentJpaEntity.java   JPA实体
  |   |-- persistence/CourseJpaEntity.java
  |   |-- persistence/CourseGradeJpaEntity.java
  |   |-- mapper/StudentMapper.java           领域<->JPA 转换
  |   |-- mapper/CourseMapper.java
  |   |-- mapper/CourseGradeMapper.java
  |   `-- repository/JpaStudentRepository.java  仓库接口的JPA实现
  |-- web/student-course
  |   |-- StudentController.java       REST 端点 + DTO
  |   |-- CourseController.java
  |   `-- CourseGradeController.java
  `-- web/IndexController.java         前端入口页
```
- domain 层零 Spring/JPA 依赖；infra 层实现仓库接口并做双模型映射。
- **备选**：方案 A（领域实体直接加 `@Entity`）——代码少，但领域与持久化耦合，不符合"标准 DDD 示例"定位，不采用。

### D2. 持久化：H2 + Spring Data JPA
- H2 内嵌（内存模式，`jdbc:h2:mem:demo`），`ddl-auto=create-drop`，测试与开发零配置。
- Spring Data JPA 提供 `JpaRepository`，仓库接口由 infra 层实现（组合 `JpaRepository` + 领域映射）。
- **备选**：SQLite——方言/DDL 支持不完善，已排除（见探索记录）。

### D3. 领域规则
- 成绩范围 0-100：`CourseGrade` 领域实体在构造/更新时校验，越界抛领域异常（如 `InvalidScoreException`）。
- 学生-课程唯一：`CourseGradeRepository` 提供 `findByStudentIdAndCourseId`；登记前校验已存在则抛领域异常（如 `DuplicateGradeException`）。
- 唯一性同时由 JPA 层加 `(student, course)` 唯一约束兜底。

### D4. REST API 形状
```
GET    /api/students          列表
POST   /api/students          创建
PUT    /api/students/{id}     更新
DELETE /api/students/{id}     删除
GET    /api/courses           列表          (同模式 CRUD)
GET    /api/students/{id}/grades   按学生查成绩
GET    /api/courses/{id}/grades    按课程查成绩
POST   /api/grades            登记成绩(0-100, 唯一)
PUT    /api/grades/{id}       修改成绩
DELETE /api/grades/{id}       删除成绩
```
- 返回 JSON；领域异常映射为 HTTP 错误（`400`）。

### D5. 前端：浏览器版 Vue3 + Tailwind（本地静态，ES 模块化组件）
```
src/main/resources/static/
  |-- index.html                    SPA 入口(单页, <script type="module"> 引入 app.js)
  |-- js/vue.global.js              本地托管的 Vue3 全局构建
  |-- css/tailwind.css              本地托管的 Tailwind 编译产物
  |-- app.css                       自定义样式
  |-- app.js                        入口模块(createApp + 挂载)
  `-- modules/
      |-- api.js                    请求工具模块(封装 fetch + 错误处理)
      |-- AppComponent.js           根组件(标签页切换 + 全局提示)
      |-- StudentList.js            学生管理组件
      |-- CourseList.js             课程管理组件
      `-- GradeList.js              成绩管理组件
```
- 浏览器版 Vue3（script 引入全局构建）无 npm/Vite 构建；Tailwind 使用本地静态文件。
- 前端采用**原生 ES 模块化组件设计**：`<script type="module">` + `import/export` 拆分模块，每个视图一个组件，共享 `api.js` 请求工具；组件用选项对象 + `template` 字符串定义（浏览器版 Vue3 全局构建含编译器，无需 SFC 编译）。
- SPA 通过 fetch 调用 REST API（同源，无跨域问题）。
- **备选**：CDN 引入 Vue/Tailwind——国内网络不可靠，采用本地托管。
- **备选**：引入 Vite/npm 构建与 `.vue` 单文件组件——推翻"无构建步骤"约束，不采用。

### D6. 测试
- 领域规则单元测试（成绩范围、唯一性）。
- 应用服务 + 仓库集成测试（`@SpringBootTest` + MockMvc，沿用现有 `webAppContextSetup` 模式；H2 内存库）。
- REST 端点测试覆盖学生/课程/成绩的成功与失败路径。

## Risks / Trade-offs

- [双模型映射代码量大] → 使用显式 Mapper 保持清晰，是 DDD 方案 B 的必要代价；后续可引入 MapStruct 缓解。
- [H2 内存库重启数据丢失] → 示例定位可接受；如需持久可切换文件模式（`jdbc:h2:file:`）。
- [浏览器版 Vue3 无构建/编译, 大型应用难维护] → 示例场景规模有限可接受；工程化时切换 Vite 工程。
- [JPA 与 SQLite 兼容坑] → 已改用 H2，规避。

## Migration Plan

绿地新增，无迁移、无回滚路径。

## Open Questions

- 无。
