# Tasks

## 1. 依赖与配置

- [ ] 1.1 在 `build.gradle.kts` 添加 `spring-boot-starter-data-jpa` 与 `com.h2database:h2` 依赖，验证 `./gradlew compileJava` 通过
- [ ] 1.2 新增 `src/main/resources/application.yml`：H2 内存数据源（`jdbc:h2:mem:demo`）与 JPA 配置（`ddl-auto=create-drop`），验证应用上下文可启动

## 2. Domain 层

- [ ] 2.1 创建领域实体 `Student`、`Course`（纯 POJO）及对应仓库接口 `StudentRepository`、`CourseRepository`，验证编译通过
- [ ] 2.2 创建领域实体 `CourseGrade`（含 0-100 成绩校验，越界抛领域异常）与 `CourseGradeRepository` 接口（含 `findByStudentIdAndCourseId`），编写成绩范围与构造校验单元测试并运行 `./gradlew test` 通过

## 3. Infrastructure 层

- [ ] 3.1 创建 JPA 实体 `StudentJpaEntity`、`CourseJpaEntity`、`CourseGradeJpaEntity`（`CourseGradeJpaEntity` 含 `(student, course)` 唯一约束）与对应 Spring Data `JpaRepository` 接口，验证编译通过
- [ ] 3.2 创建领域/JPA 双模型 Mapper（`StudentMapper`、`CourseMapper`、`CourseGradeMapper`）与仓库接口的 JPA 实现（`JpaStudentRepository`、`JpaCourseRepository`、`JpaCourseGradeRepository`），编写仓库集成测试（H2 内存库）验证增删改查与成绩唯一性兜底

## 4. Application 层

- [ ] 4.1 创建 `StudentApplicationService`、`CourseApplicationService`（`@Transactional` 编排用例），编写应用服务集成测试验证 CRUD 行为
- [ ] 4.2 创建 `CourseGradeApplicationService`（登记时校验 0-100 与重复成绩），编写登记/修改/删除/查询的成功与失败路径测试并运行 `./gradlew test` 通过

## 5. Web 层（REST）

- [ ] 5.1 创建 `StudentController`、`CourseController`（DTO + REST 端点，领域异常映射 400），编写 MockMvc 测试覆盖学生/课程 CRUD 成功与失败路径
- [ ] 5.2 创建 `CourseGradeController`（登记/修改/删除/按学生查/按课程查），编写 MockMvc 测试覆盖成绩成功与失败路径（越界、重复、不存在），运行 `./gradlew test` 全部通过

## 6. 前端页面

- [ ] 6.1 本地托管 Vue3 与 Tailwind 静态资源（`static/js/vue.global.js`、`static/css/tailwind.css`），创建 `index.html` 与 `app.js`（学生/课程/成绩三个视图 + fetch 调 REST API），验证浏览器打开页面可加载且无 404
- [ ] 6.2 编写端到端冒烟：启动应用，浏览器路径访问 `index.html`，验证学生/课程/成绩增删改查通过页面可操作，随后停止应用

## 7. 集成验证

- [ ] 7.1 运行 `./gradlew build`，验证编译、全部测试、打包全链路通过

## Workflow follow-up

- 变更实现并验证完成后，使用 /opsx-archive 归档该变更。
- 归档后运行 openspec validate 校验主 spec 合并结果。
