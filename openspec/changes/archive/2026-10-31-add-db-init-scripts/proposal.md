# Proposal

## Why

当前表结构由 Hibernate `ddl-auto: create-drop` 自动生成（黑盒、不可见），且启动后数据库为空、无任何演示数据。改用显式 SQL 脚本接管表结构，并在启动时自动预置覆盖全部功能的演示数据，使表结构可控可教学、页面打开即有数据可看。

## What Changes

- 新增 `src/main/resources/db/schema.sql`：7 张表的完整 DDL（students、courses、course_grades、books、book_copies、loans、reservations），列名、主键、外键、唯一约束与 JPA 实体映射完全一致。
- 新增 `src/main/resources/db/data.sql`：完整演示种子数据，覆盖两个上下文全部功能场景（学生/课程/成绩、图书与馆藏副本、在借记录、逾期记录、预约队列）。
- 修改 `application.yml`：`spring.jpa.hibernate.ddl-auto` 改为 `none`，配置 `spring.sql.init.mode: always` 并显式指向 `classpath:db/schema.sql` 与 `classpath:db/data.sql`。
- 每次启动（H2 内存库）自动重建表结构并载入种子数据。
- **不改变**任何 REST API、领域规则与运行行为，仅替换表结构与数据的来源。

## Capabilities

### New Capabilities

（无）

### Modified Capabilities

（无 —— 数据库初始化方式属基础设施实现细节，业务能力与 API 契约不变，故本变更声明 `skip_specs: true`。）

## Impact

- 新增文件：`src/main/resources/db/schema.sql`、`src/main/resources/db/data.sql`。
- 修改文件：`src/main/resources/application.yml`（仅 jpa.hibernate.ddl-auto 与 sql.init 配置）。
- 行为变化：应用启动时不再依赖 Hibernate 自动建表，改为执行 SQL 脚本；数据库初始含演示数据。
- 依赖与构建：无新增依赖，`./gradlew build` 结果不变。
