# Tasks

## 1. 捕获真实 DDL（对齐法）

- [ ] 1.1 临时开启 DDL 输出（`application.yml` 加 `spring.jpa.properties.hibernate.format_sql: true` 并观察日志），启动应用抓取 7 张表（students/courses/course_grades/books/book_copies/loans/reservations）的完整 `CREATE TABLE` DDL；验证：日志中 7 张表 DDL 全部捕获。
- [ ] 1.2 核对两个领域枚举（`BookCopyStatus`、`ReservationStatus`）的枚举常量名与 `@Enumerated(EnumType.STRING)` 存储值，确认 data.sql 将使用的字符串；验证：记录的枚举值与领域代码一致。

## 2. 编写初始化脚本

- [ ] 2.1 创建 `src/main/resources/db/schema.sql`：7 张表 DDL（列名、主键生成策略、外键、`uk_student_course` 唯一约束与实体映射一致；books/book_copies 主键无自增）；验证：与 1.1 捕获的 DDL 逐表比对无差异。
- [ ] 2.2 创建 `src/main/resources/db/data.sql`：完整演示集——学生 ×3、课程 ×3、成绩若干、图书 ×3 及其馆藏副本、《代码整洁之道》在借记录、李四借《算法导论》逾期记录（`CURRENT_DATE - n` 相对日期）、王五预约《算法导论》`WAITING` 预约；按外键依赖顺序插入，显式指定主键并对自增表重置 `IDENTITY` 计数；验证：SQL 能被 H2 执行无语法错误（以应用启动成功为准）。

## 3. 配置切换与端到端验证

- [ ] 3.1 修改 `application.yml`：`spring.jpa.hibernate.ddl-auto: none`，新增 `spring.sql.init.mode: always` 与 `schema-locations/data-locations` 指向 `classpath:db/`；验证：应用启动成功，日志显示执行 schema.sql 与 data.sql。
- [ ] 3.2 启动后 curl 抽查种子数据：`/api/students` 返回 3 名学生、`/api/books` 返回 3 本图书、`/api/loans/overdue` 返回李四逾期记录（含费用）、`/api/books/{算法导论id}` 预约队列含 `WAITING` 预约；验证：各端点返回预期演示数据。
- [ ] 3.3 全量回归：`./gradlew build` 全部测试通过；并通过 API 新增一条学生/图书，确认新增数据 id 不与种子数据冲突、`IDENTITY` 计数已重置；验证：BUILD SUCCESSFUL 且新增操作正常返回。
