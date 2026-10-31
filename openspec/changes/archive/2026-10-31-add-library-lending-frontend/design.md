# Design

## Context

前端为纯静态单页（`src/main/resources/static/`），基于 Vue3 全局构建 + 原生 ES 模块、无打包器。现有结构：`index.html` → `app.js` → `modules/AppComponent.js`（根组件，顶部标签导航 + 全局 `notify`），`modules/api.js`（fetch 封装），以及 `StudentList.js`/`CourseList.js`/`GradeList.js` 三个业务组件。librarylending 后端 9 个端点已就绪（见 proposal.md），缺浏览器界面。

## Goals / Non-Goals

**Goals:**

- 在现有单页内以与「学生/课程/成绩」对等的方式加入图书借阅界面，覆盖 librarylending 全部 7 条能力。
- 复用既有 `api.js`、`notify`、Tailwind 样式，不引入打包工具或新依赖。

**Non-Goals:**

- 不改动任何后端 Java 代码或 REST 端点契约。
- 不为读者选择等重复出现的两处逻辑引入共享组件抽象（见 D3）。
- 不处理鉴权、分页、搜索等超出既有页面所呈现的交互复杂度。

## Decisions

### D1：顶部加一个「图书借阅」标签，内部用次级标签分区块

顶部导航从 3 个标签扩为 4 个，新增「图书借阅」对应 librarylending 一个限界上下文；其内部再用次级标签切「图书 / 借还 / 预约与逾期」三个区块，复用与顶部相同的 `activeTab`/按钮样式模式。

理由：与「学生/课程/成绩」三个标签的对等关系最直观，也避免把 6 个标签直接摊在顶部造成语义混乱。

备选：顶部直接平铺 6 个标签（语义粒度不统一）；独立 `lending.html` 页面（需复制或抽取 api/样式/通知，见 proposal 已否）。

### D2：新增三个业务组件，平铺于 `modules/`

- `BookList.js`：图书列表、登记图书（title/author/copyCount）、查看馆藏状态与预约队列。
- `LoanList.js`：发起借阅（选读者 + 选书）、按读者查询在借记录、归还。
- `ReservationList.js`：创建预约（选读者 + 选书）、逾期列表（含费用）。

每个组件与 `StudentList.js` 同构：接收 `notify` prop、`onMounted` 拉取数据、`request()` 交互。组件与现有文件一致地直接放到 `modules/` 平铺，不新建目录（呼应前端既有风格）。

### D3：读者选择复用 `/api/students`，各组件独立拉取，不抽共享组件

借阅与预约都需要选读者，而读者即学生，数据源为 `/api/students`。两处各自在 `onMounted` 拉一次学生列表做下拉，不抽 `ReaderSelect` 共享组件。

理由：仅两处、代码量小（一次 fetch + 一个 `<select>`），抽组件反而引入 props/事件协议开销（简单优先）。

### D4：字段命名与后端 DTO 保持一致

前端组件读写字段直接对齐后端 `BookView`/`BookRequest`/`LoanRequest` 等定义（如 `title`、`author`、`copyCount`、`readerId`、`bookId`），请求体 JSON 键名与 DTO record 组件名一致，零映射。

## Risks / Trade-offs

- [读者下拉依赖学生数据] → 学生为空时，借阅/预约下拉为空；按钮提交因 `readerId` 缺失被后端拒绝，前端以 `notify` 给出可理解提示（如「请先在学生管理中创建学生」）。
- [单页跨上下文耦合（图书借阅标签依赖学生 API）] → 已接受：同一应用、同一后端、同一 `JpaStudentRepository`，后端本就通过防腐层复用学生仓储；前端同样直接调用是等价简化。
- [组件体量偏大（例如 LoanList 同时含借阅与归还）] → 教学项目可接受，按能力而非行数划分，保持一个组件对应一组内聚操作。

## Migration Plan

纯静态资源新增与两个既有文件（`index.html`、`AppComponent.js`）的标题/导航修改，无数据库、无配置、无部署步骤。回滚为 `git revert`。

## Open Questions

（无）