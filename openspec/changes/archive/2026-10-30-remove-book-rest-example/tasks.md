# Tasks

## 1. 删除 Book 示例代码

- [x] 1.1 删除 `src/main/java/cn/intellijboy/demo/web/Book.java`、`BookRequest.java`、`BookController.java` 三个源码文件，验证 `./gradlew compileJava` 通过
- [x] 1.2 删除 `src/test/java/cn/intellijboy/demo/web/BookControllerTests.java` 测试文件，验证 `./gradlew compileTestJava` 通过
- [x] 1.3 全源码 grep 确认 `src/` 下已无 `Book`/`/api/books` 引用，验证无残留引用

## 2. 同步规格

- [x] 2.1 执行 `openspec sync specs --change "remove-book-rest-example"`，将 Book 5 条需求的 REMOVED delta 写入 `openspec/specs/build-infrastructure/spec.md`，验证该 spec 已无任何 Book 需求、需求数由 12 降至 7
- [x] 2.2 执行 `openspec validate`，验证变更与主 spec 均通过校验

## 3. 整体验证

- [x] 3.1 运行 `./gradlew build`（含全部测试），验证编译、测试全绿且 `studentcourse` 相关测试不受影响
- [x] 3.2 复核 `web` 包仅剩 `studentcourse` 子包与 `DemoController`，`/demo` 与 `/api/students` 等学生课程成绩端点行为不变

## Workflow follow-up

- 变更经评审确认后归档（`openspec archive`），归档前确认主 spec 中无 Book 需求残留。
- 归档后复核归档目录与主 spec 内容。
