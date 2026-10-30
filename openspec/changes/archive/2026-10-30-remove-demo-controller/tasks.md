# Tasks

## 1. 删除 /demo 端点代码

- [x] 1.1 删除 `src/main/java/cn/intellijboy/demo/web/DemoController.java`，验证 `./gradlew compileJava` 通过
- [x] 1.2 修改 `StudentCourseManagementApplicationTests`：删除 `demoEndpointReturnsSuccess` 测试方法及不再使用的 `get`、`content` 静态导入，保留 `contextLoads`，验证 `./gradlew compileTestJava` 通过
- [x] 1.3 全源码 grep 确认 `src/` 下已无 `DemoController`/`/demo` 端点引用（`jdbc:h2:mem:demo` 为数据库名，不在此列），验证无残留引用

## 2. 同步规格

- [x] 2.1 执行 `openspec sync specs --change "remove-demo-controller"`，将 REMOVED「空壳应用启动与 HTTP 响应」与 ADDED「应用可启动」写入 `openspec/specs/build-infrastructure/spec.md`，验证需求数仍为 7 且原需求已被替换
- [x] 2.2 直接编辑主 spec 的 Purpose：将「以及空壳应用的启动与 HTTP 响应能力」调整为与「应用可启动」一致的表述，验证 `openspec list --specs` 中该 spec 通过
- [x] 2.3 执行 `openspec validate`，验证变更与两个主 spec 均通过校验

## 3. 整体验证

- [x] 3.1 运行 `./gradlew build`（含全部测试），验证编译、测试全绿，测试数由 47 降至 46（仅减少 `demoEndpointReturnsSuccess`），`studentcourse` 相关测试不受影响
- [x] 3.2 复核 `web` 包仅剩 `studentcourse` 子包，无任何业务无关示例类

## Workflow follow-up

- 建议先归档前一变更 `remove-book-rest-example`（其 delta 已同步，归档时重复 REMOVED 会被幂等跳过）。
- 本变更经评审确认后归档（`openspec archive`），归档前确认主 spec 无「空壳应用」需求与 Purpose 残留。
- 归档后复核归档目录与主 spec 内容。
