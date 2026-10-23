# Tasks

## 1. 模型与内存仓库

- [ ] 1.1 创建 `cn.intellijboy.demo.domain.Book`（record: id、title、author）与 `cn.intellijboy.demo.repository.BookRepository` 接口（findAll/findById/save/deleteById），验证 `./gradlew compileJava` 通过
- [ ] 1.2 创建 `cn.intellijboy.demo.repository.InMemoryBookRepository`（`@Repository`，`ConcurrentHashMap` + `AtomicLong`），验证 `./gradlew compileJava` 通过

## 2. REST 端点实现

- [ ] 2.1 创建 `cn.intellijboy.demo.web.BookController` 的查询端点：`GET /api/books` 返回 200 与列表、`GET /api/books/{id}` 返回 200 与 Book 或 404，验证编译通过
- [ ] 2.2 在 `BookController` 中实现写端点：`POST /api/books` 返回 201 与新建 Book（id 服务端生成）、`PUT /api/books/{id}` 返回 200 与更新后 Book 或 404、`DELETE /api/books/{id}` 返回 204 或 404，验证编译通过

## 3. 测试

- [ ] 3.1 编写 `BookControllerTests`：覆盖列表/单个/创建/更新/删除的成功路径与 404 路径（MockMvc 模式，沿用现有 `webAppContextSetup` 写法），运行 `./gradlew test` 验证全部通过

## 4. 集成验证

- [ ] 4.1 运行 `./gradlew build`，验证编译、测试、打包全链路通过
- [ ] 4.2 启动应用，依次请求各 Book 端点（含 404 场景）验证返回码符合预期，随后停止应用

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更。
- 归档后运行 openspec validate 校验主 spec 合并结果。
