# Spec Delta

## REMOVED Requirements

### Requirement: 空壳应用启动与 HTTP 响应
工程 SHALL 提供可直接启动的空壳应用，启动后 SHALL 监听端口并对 HTTP 请求返回成功响应。

**Reason**: 空壳端点 `/demo` 是早期脚手架的连通性验证产物，随示例代码清理一并移除；工程已进入学生课程成绩单一业务模块阶段，不再保留空壳应用形态。
**Migration**: 无替代端点；`GET /demo` 不再可用，请勿依赖该接口。应用启动验证能力由新需求「应用可启动」承接。

#### Scenario: 应用正常启动
- **WHEN** 开发者启动应用
- **THEN** 应用成功绑定配置端口并无异常退出

#### Scenario: HTTP 请求得到响应
- **WHEN** 客户端向应用发送 HTTP 请求
- **THEN** 应用返回成功状态码的响应

## ADDED Requirements

### Requirement: 应用可启动
工程 SHALL 提供可直接启动的 Spring Boot 应用，启动后 SHALL 成功绑定配置端口且应用上下文加载无异常。

#### Scenario: 应用正常启动
- **WHEN** 开发者启动应用
- **THEN** 应用成功绑定配置端口并无异常退出
