# Tasks

## 1. 项目名重构

- [x] 1.1 将 `settings.gradle.kts` 的 `rootProject.name` 由 `rbac-openspec-demo` 改为 `student-course-management`，验证 `./gradlew bootJar` 产物名为 `student-course-management-*.jar` 且构建无报错
- [x] 1.2 全仓库检索确认无遗漏的活动代码引用 `rbac-openspec-demo`（归档记录除外），验证 `./gradlew clean` 后构建缓存无旧名残留

## 2. 主应用类与测试类重命名

- [x] 2.1 将 `src/main/java/cn/intellijboy/demo/RbacOpenspecDemoApplication.java` 重命名为 `StudentCourseManagementApplication.java`（类名与文件名同步），验证 `./gradlew compileJava` 通过
- [x] 2.2 将 `src/test/java/cn/intellijboy/demo/RbacOpenspecDemoApplicationTests.java` 重命名为 `StudentCourseManagementApplicationTests.java`（类名与文件名同步），验证 `./gradlew test` 通过

## 3. 前端页面标题对齐

- [x] 3.1 将 `src/main/resources/static/index.html` 的 `<title>` 由"学生课程成绩管理系统"改为"学生课程管理系统（Student Course Management）"，启动应用访问页面验证标题显示正确且学生/课程/成绩视图可正常加载

## 4. 集成验证

- [x] 4.1 运行 `./gradlew clean build`，验证编译、全部测试、打包全链路通过，且 jar 产物名为 `student-course-management-*.jar`

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更。
- 归档后运行 openspec validate 校验主 spec 合并结果（本变更为 skip_specs 重构，无 spec 合并，归档后应仍通过校验）。
