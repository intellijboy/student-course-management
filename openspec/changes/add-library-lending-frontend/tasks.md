# Tasks

## 1. 导航与壳改造

- [ ] 1.1 修改 `index.html` 的 `<title>` 与 `AppComponent.js` 顶部 `<h1>` 为覆盖两个上下文的统一名称（如「图书馆管理系统」）；验证：刷新页面后浏览器标签页标题与页面主标题均已更新。
- [ ] 1.2 在 `AppComponent.js` 顶部导航新增第 4 个「图书借阅」标签（内部以次级标签切换「图书 / 借还 / 预约与逾期」），导入并挂载 `BookList`/`LoanList`/`ReservationList`；验证：页面出现「图书借阅」标签，可切换到三个次级区块（组件可先为空壳）。

## 2. 图书管理组件

- [ ] 2.1 创建 `modules/BookList.js`：`onMounted` 调 `GET /api/books` 渲染图书列表，登记表单（title/author/copyCount）调 `POST /api/books` 后刷新列表；验证：新增图书后列表可见该图书记录，字段正确回显。
- [ ] 2.2 在该组件为每本书提供「查看馆藏」：调 `GET /api/books/{id}`，弹窗展示馆藏总数、可借数、在借数与预约队列；验证：点击某书弹窗显示其馆藏状态与预约队列（队列为空时给出空态提示）。

## 3. 借还管理组件

- [ ] 3.1 创建 `modules/LoanList.js`：借阅表单含「读者」下拉（`GET /api/students` 数据源）与「图书」下拉（`GET /api/books`），提交调 `POST /api/loans`（readerId/bookId）；另提供按读者查询，调 `GET /api/readers/{id}/loans` 展示在借记录；验证：选读者与图书可成功借阅，该读者名下出现对应在借记录，读者为空时提交给出可理解提示。
- [ ] 3.2 在在借记录上提供「归还」按钮，调 `POST /api/loans/{id}/return` 并刷新；验证：归还后该记录从读者在借列表中消失。

## 4. 预约与逾期组件

- [ ] 4.1 创建 `modules/ReservationList.js`：预约表单含「读者」下拉与「图书」下拉，提交调 `POST /api/reservations`（readerId/bookId）；并调 `GET /api/loans/overdue` 展示逾期列表（含逾期费用）；验证：可创建预约并刷新后在前端得到确认，逾期列表正确展示记录与费用（无逾期时为空态）。

## 5. 全量回归

- [ ] 5.1 执行 `./gradlew build` 确认静态资源与后端集成无破坏；验证：构建成功。
- [ ] 5.2 启动应用，逐页签走查：图书登记/馆藏查询、借阅/归还、预约、逾期查询全部可用且与后端交互无报错；验证：覆盖 spec「页面访问与功能可用」场景。