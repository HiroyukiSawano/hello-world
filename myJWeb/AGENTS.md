# Repository Guidelines

## 项目结构与模块组织
- 采用 Maven 标准布局；业务代码在 `src/main/java/org/example/myjweb`，入口类为 `MyJWebApplication`；资源配置位于 `src/main/resources`（`application.properties` 可追加数据源、端口等私有覆盖）。
- 测试代码与包结构对齐放在 `src/test/java`，便于与生产代码一一对应；Spring REST Docs 片段会默认输出到 `target/generated-snippets`（用于 Asciidoctor 生成文档）。
- 构建脚本和依赖声明集中在 `pom.xml`；使用 `.mvn/wrapper` 让团队成员无需预装 Maven；生成物与日志写入 `target/`，避免提交。

## 构建、测试与本地开发命令
- `./mvnw clean verify`：标准全量构建，执行单元/集成测试并准备打包工件，是 PR 前的必跑命令。
- `./mvnw spring-boot:run`：本地开发热启动，配合 `spring-boot-devtools` 支持快速重载；必要时通过 JVM 参数覆盖端口或数据源。
- `./mvnw test`：快速回归已有测试集，适合开发中高频验证；如需生成接口文档可运行 `./mvnw prepare-package` 触发 Asciidoctor。

## 编码风格与命名约定
- Java 21，统一使用 4 空格缩进，UTF-8 文件编码；包名小写，用领域分层（`controller/service/repository/config`）保持清晰。
- 类名、枚举名用帕斯卡命名，方法与变量用驼峰；常量全大写下划线分隔；Spring 组件推荐构造器注入，避免字段注入。
- 若使用 Lombok，显式保留可读性（如 `@Getter @Setter`）；接口返回值保持明确的 DTO，避免直接暴露实体。

## 测试指南
- 采用 JUnit 5 与 `spring-boot-starter-test`；安全相关场景可用 `spring-security-test` 的 `@WithMockUser` 或 `SecurityMockMvcRequestPostProcessors`.
- 测试类命名以 `*Tests` 结尾，方法可用 `should…`/`when…` 叙述行为；控制器层可用 MockMvc，必要时生成 REST Docs 片段以保证文档同步。
- 提交前至少运行 `./mvnw test`；涉及数据库的测试尽量使用内存数据库或 Testcontainers，避免依赖本地真实环境。
- Service 逻辑优先做纯单元测试；Controller 使用 `@WebMvcTest` + MockMvc，依赖用 `@MockitoBean` 替身，安全场景用 `@WithMockUser`。
- Mapper 测试默认走 H2（`application-test.properties` + `schema.sql`），使用 `@SpringBootTest` + `@ActiveProfiles("test")` + `@Transactional`。

## 日志规范
- 统一使用 SLF4J（`LoggerFactory` 或 `@Slf4j`），避免 `System.out/err`。
- Logback 配置集中在 `src/main/resources/logback-spring.xml`，采用按天 + 单文件 50MB 的滚动策略，保留 30 天并限制总量 2GB。
- 本地日志输出路径通过 `logging.file.path` 或 `logging.file.name` 在 `application-local.properties` 中配置，默认落盘 `target/logs`。
- 日志级别在本地配置里通过 `logging.level.*` 调整，避免将敏感日志提交入库。

## 提交与 PR 指南
- 提交信息保持祈使句简洁（如 `Add login endpoint`）；一个提交聚焦一类变更，避免将格式化与业务混杂。
- PR 描述需包含：变更目的、主要实现点、风险/兼容性提示、已执行的命令清单（附 `./mvnw …` 结果），若涉及接口/界面请补充请求示例或截图。
- 链接相关 Issue 或需求单；如变更影响配置或启动方式，请在描述中给出升级步骤，方便评审与上线。

## 安全与配置提示
- 不要提交任何凭据；本地敏感配置放在未提交的 `application-local.properties` 或环境变量（例如 `SPRING_DATASOURCE_URL/USERNAME/PASSWORD`），并在 README/PR 中注明依赖。
- Spring Security 已启用，新增端点请显式配置访问策略并补充正反向用例；数据库驱动依赖 MySQL，确保连接使用 SSL 或内网地址。
- 生成的 Asciidoctor 文档仅用于发布阶段，避免将 `target/` 内容加入版本库；如需共享生成结果，请上传到制品库或附件而非提交源码。



## Progress Tracking
- Review `PROGRESS.md` at the start of work and update it after completing milestones.
