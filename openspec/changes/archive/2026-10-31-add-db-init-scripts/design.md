# Design

## Context

数据库为 H2 内存库（`jdbc:h2:mem:demo`），当前 `ddl-auto: create-drop` 由 Hibernate 自动建表。8 个 JPA 实体映射 7 张表，其中列名存在**混合策略**：`course_grades.student_id/course_id` 与 `book_copies.bookId` 为显式注解（前者下划线、后者驼峰），`loans`/`reservations` 等走 Spring Boot 默认下划线物理命名策略。变更动机见 proposal.md。

## Goals / Non-Goals

**Goals:**

- 表结构完全由 `db/schema.sql` 接管，DDL 与实体映射 100% 一致。
- 启动即自动载入完整演示数据（覆盖全部功能场景）。
- 保留 `./gradlew build` 与全部现有测试通过。

**Non-Goals:**

- 不引入 Flyway/Liquibase 等版本化迁移工具（H2 内存库每次重建，脚本即足够）。
- 不改任何 JPA 实体映射、REST API 与业务规则。

## Decisions

### D1：DDL 以 Hibernate 实际生成结果为准（对齐法），不手工推断列名

schema.sql 的列名与约束**不能靠猜**（实体列名驼峰/下划线混用）。实施第一步：保持当前配置启动一次，抓取 Hibernate 日志中的 `create table` DDL（`spring.jpa.properties.hibernate.format_sql: true` + `hibernate.hbm2ddl.auto` 打印），将其整理为 `db/schema.sql`。

理由：这是列名、外键、唯一约束、主键生成策略（books/book_copies 无自增 vs 其余 IDENTITY）的唯一可靠来源。

### D2：脚本放入 `src/main/resources/db/`，通过 `schema-locations`/`data-locations` 显式指定

```
src/main/resources/db/
├── schema.sql
└── data.sql
```

配置：
```yaml
spring:
  sql:
    init:
      mode: always
      schema-locations: classpath:db/schema.sql
      data-locations: classpath:db/data.sql
```

`mode: always` 显式确保每次启动执行（H2 内存库天然幂等：每次启动新建库）。

### D3：data.sql 按外键依赖顺序插入，显式指定主键

插入顺序：`students/courses` → `course_grades` → `books` → `book_copies` → `loans` → `reservations`。`books`/`book_copies` 主键无自增（id 由领域代码分配），种子数据必须显式给 id；`loans`/`reservations` 等自增表可显式指定 id 以保持引用关系稳定。

### D4：种子数据覆盖全部演示场景（完整演示集）

- 学生 ×3、课程 ×3、成绩若干 → student-course 上下文全功能有数据。
- 图书 ×3（《深入理解Java虚拟机》×2 副本、《代码整洁之道》×3、《算法导论》×1）。
- 在借记录 ×1（张三借《代码整洁之道》）；**逾期记录** ×1（李四借《算法导论》超期未还，可展示逾期费用）。
- **预约队列** ×1（王五预约《算法导论》，该书唯一副本被借出 → `WAITING`）。

日期基准：借阅/逾期日期相对启动日期构造（如 `CURRENT_DATE - n`），保证"逾期"在任意启动时刻都成立。

## Risks / Trade-offs

- [DDL 与实体映射不一致 → 启动或运行时报 SQL 错误] → D1 对齐法从源头规避；以应用启动成功 + 全量测试通过为完成标准。
- [`loans`/`reservations` 的自增 id 与种子显式 id 冲突] → H2 显式插入 id 后需重置 `IDENTITY` 计数（`ALTER TABLE ... ALTER COLUMN id RESTART WITH n`），否则后续新增记录主键冲突。
- [data.sql 中枚举值与领域代码不一致] → 枚举以 `@Enumerated(EnumType.STRING)` 存储，值取 `BookCopyStatus`/`ReservationStatus` 实际枚举名（`AVAILABLE`/`BORROWED`/`WAITING` 等），实施时核对领域枚举定义。
- [逾期演示依赖系统时间] → 用相对日期（`CURRENT_DATE - n`）构造，任意启动时刻均演示成立。

## Migration Plan

单应用配置变更，无部署迁移。回滚为 `git revert`（恢复 `ddl-auto: create-drop` 并删除脚本）。

## Open Questions

（无）
