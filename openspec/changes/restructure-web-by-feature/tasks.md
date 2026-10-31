# Tasks

## 1. librarylending 接口层拆分

- [ ] 1.1 将 `BookController`、`BookRequest` 移入 `librarylending/web/book/`（调整 package 声明与相关 import），并同步修正 `LibraryLendingExceptionHandler` 中对 `BookController` 的跨包 import；验证：全量编译通过。
- [ ] 1.2 将 `LoanController`、`LoanRequest` 移入 `librarylending/web/loan/`，同步修正 Handler 的跨包 import；验证：全量编译通过。
- [ ] 1.3 将 `ReservationController`、`ReservationRequest` 移入 `librarylending/web/reservation/`，同步修正 Handler 的跨包 import；验证：全量编译通过。
- [ ] 1.4 拆分 `Dtos.java`：7 个嵌套 record 原样提升为单文件顶层类并归入功能子包（`BookView`、`BookCopyView`、`BookStatusView` → `book/`；`LoanView`、`OverdueView`、`ReturnView` → `loan/`；`ReservationView` → `reservation/`），更新全部引用后删除 `Dtos.java`；验证：全量编译通过且 `src/` 下无 `Dtos` 符号残留。
- [ ] 1.5 运行 librarylending 上下文测试（`LibraryLendingControllerTest`、`LendingEndToEndTest`）；验证：全部通过，无行为变化。

## 2. studentcourse 接口层拆分与命名统一

- [ ] 2.1 将 `StudentController`、`StudentRequest` 移入 `studentcourse/web/student/`，`StudentDto` 更名为 `StudentView` 并随迁，修正 Controller 与测试中的引用；验证：全量编译通过。
- [ ] 2.2 将 `CourseController`、`CourseRequest` 移入 `studentcourse/web/course/`，`CourseDto` 更名为 `CourseView` 并随迁，修正引用；验证：全量编译通过。
- [ ] 2.3 将 `CourseGradeController`、`GradeQueryController`、`GradeRequest`、`GradeUpdateRequest` 移入 `studentcourse/web/grade/`，`CourseGradeDto` 更名为 `CourseGradeView` 并随迁，修正引用；验证：全量编译通过。
- [ ] 2.4 运行 studentcourse 上下文测试（`StudentCourseControllerTest`、`CourseGradeControllerTest`）；验证：全部通过，无行为变化。

## 3. 全量回归与结构复核

- [ ] 3.1 执行全量构建与测试（`./gradlew build`）；验证：构建成功且全部测试通过。
- [ ] 3.2 复核最终结构与 design.md 决策一致：14 个类位于 6 个功能子包，2 个 ExceptionHandler 留 `web` 根包，4 个测试类留 `src/test/.../web` 根包，无 `XxxDto` 命名残留；验证：逐项目录清单核对完成。
