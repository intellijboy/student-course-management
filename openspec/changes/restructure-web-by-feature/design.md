# Design

## Context

两个限界上下文（`librarylending`、`studentcourse`）的接口层现状与目标划分见 proposal.md。关键约束：

- 项目为 DDD 四层结构（`web` → `application` → `domain` ← `infrastructure`），`web` 是唯一的接口层入口。
- `librarylending` 的出参 record 全部嵌套在 `Dtos.java` 聚合类中；`studentcourse` 的出参为逐文件 `XxxDto`，命名风格与 librarylending 的 `XxxView` 不一致。
- Spring Boot 组件扫描基于根包 `cn.intellijboy.demo`，移动类到子包不影响 Bean 发现。
- 测试为 4 个跨资源的集成/E2E 测试（`LibraryLendingControllerTest`、`LendingEndToEndTest`、`StudentCourseControllerTest`、`CourseGradeControllerTest`），每个测试覆盖同一上下文的多个 Controller。

## Goals / Non-Goals

**Goals:**

- 同一功能的 Controller、Request、View 落在同一个子包内，使功能变更的代码改动收敛于单一包。
- 拆除 `Dtos.java` 聚合类，统一两个上下文的出参为单文件 `XxxView` record。
- 保持两个 ExceptionHandler 的横切位置与行为。

**Non-Goals:**

- 不统一两个 ExceptionHandler 的作用域机制差异（librarylending 用 `assignableTypes` 显式列举，studentcourse 为全局 `@RestControllerAdvice`）——这是行为层面的差异，超出纯结构重构范围。
- 不引入任何校验注解、响应包装、API 版本化等接口层新能力。
- 不动 `application`/`domain`/`infrastructure` 三层的任何代码。

## Decisions

### D1：按功能/资源分包，而非按角色分包

采用 `web/book/`、`web/loan/` … 的功能维度划分，拒绝 `controller/` + `dto/` + `request/` 的角色维度划分。

理由：角色分包下每新增一个功能，所有子包各长一格，且一次功能变更需跨 3~4 个包跳转——耦合方向与变更方向垂直。功能分包使同功能元素内聚，与 DDD 按上下文/聚合组织代码的思路一致。

备选：保持平铺（在体量封顶的前提下也成立），但用户已确认采用方案 B，为后续演进预留结构。

### D2：出参命名统一为 `XxxView`，入参保持 `XxxRequest`

- 拆出的 librarylending record 自然命名为 `BookView` 等，无需变化。
- `StudentDto` → `StudentView`、`CourseDto` → `CourseView`、`CourseGradeDto` → `CourseGradeView`。

理由：`View` 表达"领域模型的对外视图"语义，与既有 `Dtos.java` 的 Javadoc 一致；`Dto` 过于笼统。入参 `XxxRequest` 两个上下文已一致，不动。

备选：全部改名为 `XxxDto`（改动量相同），但 `View` 语义更准且是 librarylending 已建立的风格。

### D3：ExceptionHandler 留在 `web` 根包

- `LibraryLendingExceptionHandler` 横跨三个功能子包（`assignableTypes` 显式引用三个 Controller），放入任一子包都会制造子包间横向依赖。
- `GlobalExceptionHandler` 为全上下文生效的横切关注点。
- 连带改动仅限：`assignableTypes` 与 Controller 分属不同子包后，import 从同包直引改为跨子包 import。

### D4：测试留在 `src/test/.../web` 根包

4 个测试类均为跨资源集成/E2E 测试，其作用域与 `web` 根包（而非单个功能子包）匹配。仅同步更新其 import 与 `XxxDto` → `XxxView` 引用。

### D5：`Dtos.java` 拆分方式

嵌套 record 原样提升为顶层类，一个 record 一个文件，`public final class Dtos` 与其私有构造器删除。各 record 的静态工厂方法（`of(...)`）与字段原样保留。

### D6：划分边界以功能职责为准，不以 URL 前缀为准

`GradeQueryController` 虽挂载 `/api/students/{id}/grades`，但其依赖与职责均为成绩资源，归入 `grade/` 而非 `student/`。

## Risks / Trade-offs

- [分包后包可见性变化——若某类成员是包私有且跨子包引用] → 现状所有相关类均为 `public`（record 全部顶层或嵌套 public，Controller/Handler 均为 public），无包私有成员，风险不存在；实施时若发现包私有成员则提升为 public 并在 PR 说明。
- [`Dtos.java` 拆分遗漏引用] → 拆分后 `Dtos` 符号不再存在，编译器强制暴露所有遗漏引用，以全量编译通过为完成标准。
- [改名导致测试断言或 Jackson 序列化受影响] → record 的 JSON 序列化由组件名决定，与类名无关；`@RestControllerAdvice` 行为不因移动包而变。以现有 4 个测试全部通过为回归标准。
- [Git 历史追溯（move + 改名混合）] → 分包的移动与 `Dto` 改名尽量分开提交，`git log --follow` 可正常追溯。

## Migration Plan

单应用内纯重构，无部署迁移。实施顺序：librarylending 拆分 → studentcourse 拆分与改名 → 全量编译 → 4 个测试通过。回滚策略：`git revert` 即可，无数据或配置状态。

## Open Questions

（无）
