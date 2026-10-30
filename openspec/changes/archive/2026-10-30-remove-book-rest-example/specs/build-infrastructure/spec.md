# Spec Delta

## REMOVED Requirements

### Requirement: Book 列表查询
系统 SHALL 提供端点返回 Book 资源列表，每个条目包含 id、标题、作者。

**Reason**: Book CRUD 为早期 REST 入门示例，已被学生课程成绩模块取代，工程仅保留单一业务模块。
**Migration**: 无替代端点；`/api/books` 不再可用，请勿依赖该接口。

#### Scenario: 返回书目列表
- **WHEN** 客户端请求 Book 列表端点
- **THEN** 系统返回 200 及全部 Book 条目

### Requirement: Book 单个查询
系统 SHALL 提供端点按 id 返回单个 Book；id 不存在时 SHALL 返回 404。

**Reason**: Book CRUD 为早期 REST 入门示例，已被学生课程成绩模块取代，工程仅保留单一业务模块。
**Migration**: 无替代端点；`/api/books/{id}` 不再可用，请勿依赖该接口。

#### Scenario: 查询存在的 Book
- **WHEN** 客户端请求某个已存在 id 的 Book
- **THEN** 系统返回 200 及该 Book 的完整信息

#### Scenario: 查询不存在的 Book
- **WHEN** 客户端请求一个不存在的 id
- **THEN** 系统返回 404

### Requirement: Book 创建
系统 SHALL 提供端点创建 Book，创建成功 SHALL 返回 201 及新创建资源的 id。

**Reason**: Book CRUD 为早期 REST 入门示例，已被学生课程成绩模块取代，工程仅保留单一业务模块。
**Migration**: 无替代端点；`POST /api/books` 不再可用，请勿依赖该接口。

#### Scenario: 创建 Book
- **WHEN** 客户端提交包含标题与作者的 Book 数据
- **THEN** 系统创建该书目并返回 201 与新资源的 id

### Requirement: Book 更新
系统 SHALL 提供端点按 id 更新 Book 的标题与作者；id 不存在时 SHALL 返回 404。

**Reason**: Book CRUD 为早期 REST 入门示例，已被学生课程成绩模块取代，工程仅保留单一业务模块。
**Migration**: 无替代端点；`PUT /api/books/{id}` 不再可用，请勿依赖该接口。

#### Scenario: 更新存在的 Book
- **WHEN** 客户端提交对已存在 Book 的标题或作者修改
- **THEN** 系统更新该书目并返回 200 及更新后的信息

#### Scenario: 更新不存在的 Book
- **WHEN** 客户端请求更新一个不存在的 id
- **THEN** 系统返回 404

### Requirement: Book 删除
系统 SHALL 提供端点按 id 删除 Book；删除成功 SHALL 返回 204，id 不存在时 SHALL 返回 404。

**Reason**: Book CRUD 为早期 REST 入门示例，已被学生课程成绩模块取代，工程仅保留单一业务模块。
**Migration**: 无替代端点；`DELETE /api/books/{id}` 不再可用，请勿依赖该接口。

#### Scenario: 删除存在的 Book
- **WHEN** 客户端请求删除一个已存在的 id
- **THEN** 系统删除该书目并返回 204

#### Scenario: 删除不存在的 Book
- **WHEN** 客户端请求删除一个不存在的 id
- **THEN** 系统返回 404
