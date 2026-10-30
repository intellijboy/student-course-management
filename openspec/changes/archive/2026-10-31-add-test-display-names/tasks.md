# Tasks

## 1. librarylending 模块测试注解

- [x] 1.1 为 `librarylending.domain.*` 测试类添加类级 `@DisplayName`，并为全部 `@Test` 方法添加行为描述式中文 `@DisplayName`（`ValueObjectTest`/`BookTest`/`LoanTest`/`ReservationTest`/`DomainEventTest`/`BorrowingPolicyAndFactoryTest`）——验证：`./gradlew test` 通过且报告显示中文名
- [x] 1.2 为 `librarylending.application.*` 测试类添加类级 `@DisplayName`，并为全部 `@Test` 方法添加中文 `@DisplayName`（`CatalogApplicationServiceTest`/`LoanApplicationServiceTest`/`LendingQueryServiceTest`）——验证：`./gradlew test` 通过
- [x] 1.3 为 `librarylending.infrastructure.LendingRepositoryIntegrationTest` 添加类级与方法级中文 `@DisplayName`——验证：`./gradlew test` 通过
- [x] 1.4 为 `librarylending.web.*` 测试类添加类级 `@DisplayName`，并为全部 `@Test` 方法添加中文 `@DisplayName`（`LibraryLendingControllerTest`/`LendingEndToEndTest`）——验证：`./gradlew test` 通过

## 2. studentcourse 模块测试注解

- [x] 2.1 为 `studentcourse.domain.CourseGradeTest` 添加类级与方法级中文 `@DisplayName`——验证：`./gradlew test` 通过
- [x] 2.2 为 `studentcourse.application.*` 测试类添加类级 `@DisplayName`，并为全部 `@Test` 方法添加中文 `@DisplayName`（`StudentCourseApplicationServiceTest`/`CourseGradeApplicationServiceTest`/`GradeEventPublishTest`）——验证：`./gradlew test` 通过
- [x] 2.3 为 `studentcourse.infrastructure.RepositoryIntegrationTest` 添加类级与方法级中文 `@DisplayName`——验证：`./gradlew test` 通过
- [x] 2.4 为 `studentcourse.web.*` 测试类添加类级 `@DisplayName`，并为全部 `@Test` 方法添加中文 `@DisplayName`（`StudentCourseControllerTest`/`CourseGradeControllerTest`）——验证：`./gradlew test` 通过
- [x] 2.5 为启动类测试 `StudentCourseManagementApplicationTests` 添加类级与方法级中文 `@DisplayName`——验证：`./gradlew test` 通过

## 3. 全量回归与核对

- [x] 3.1 运行全量测试回归，确认添加 `@DisplayName` 后所有测试仍通过且行为不变——验证：`./gradlew test` 全绿，测试报告展示中文名
- [x] 3.2 核对 `docs/ddd-tutorials/08-example-project-tour.md` 测试地图中的测试名与实际类名一致（如需要同步中文名）——验证：文档与测试类名一致

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更（skip_specs 重构，无 spec 合并）。
- 归档后运行 openspec validate 校验主 spec（应仍通过）。
