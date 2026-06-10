# AGENTS.md

本文件是本仓库的 AI 编码协作规范。修改代码时优先遵循这里的约定，并结合现有代码风格执行。

## 项目概况

- 这是一个 Java 21 + Spring Boot 3 后端模板项目。
- 构建工具为 Maven，主模块位于 `src/main/java/com/tech`。
- 持久层使用 MyBatis Plus，Mapper XML 位于 `src/main/resources/mapper`。
- 环境配置由 `src/main/resources/application.yml` 和 `application-{profile}.yml` 组成。
- 本地开发 profile 为 `local`，默认本地端口参考 `readme.md`。

## 代码风格

- 优先保持现有包结构、命名方式、注解使用和 Lombok 使用习惯。
- 不做与当前任务无关的重构、格式化或依赖升级。
- 新增类和方法应放在与现有职责一致的包下，避免创建不必要的新抽象。
- 复用项目已有的工具类、常量、枚举、异常、响应包装和配置方式。
- 只在逻辑不容易一眼看懂时添加简短注释，不添加重复代码含义的注释。

## 分层约定

- Controller 只处理请求入口、参数校验、认证上下文和响应返回。
- Qo 只作为 Controller 入参模型使用，不直接传入 `service` 层；Controller 应将 Qo 拆解为明确业务参数或转换为职责合适的对象后再调用服务。
- 业务逻辑放在 `service` 层；按现有风格区分 Query、Command、Assembler。
- 数据访问优先通过 `repository/dao` 和 `repository/mapper`，不要在 Controller 中直接访问 Mapper。
- Entity、Qo、Bo、Vo 各自保持职责边界，不要混用。
- MyBatis Plus 查询优先沿用现有 DAO/Mapper 风格；需要 XML SQL 时放入 `resources/mapper`。

## 接口与响应

- 保持现有统一响应机制，优先使用 `JsonResult`、`JsonPage`、`BizException`、`ErrorCode`、`SystemCode` 等已有结构。
- 新接口应参考现有 Controller 的路由、注解、参数对象和返回方式。
- HTTP 接口默认统一使用 `POST` 请求；如需使用其他方法，应有明确的业务或兼容性原因。
- 接口命名遵循统一动词前缀：获取单个对象使用 `get`，获取列表使用 `list`，分页查询使用 `query`，新增使用 `save`，修改使用 `update`，删除使用 `delete`。
- 分页查询参数统一使用 `pageNum`、`pageSize`，默认值分别为 `1`、`20`。
- 方法参数同时包含业务查询条件和分页参数时，`pageNum`、`pageSize` 放在参数列表末尾。
- 参数校验优先使用现有校验依赖和项目内已有模式。
- 不随意改变已有接口路径、请求参数、响应字段或错误码语义，除非任务明确要求。

## 安全与配置

- 不提交真实密钥、Token、数据库密码、OSS 密钥或生产环境敏感配置。
- 修改 `application-prod.yml`、部署脚本、Docker 配置、Nginx 配置时要格外谨慎，并在结果中说明影响。
- 日志中不要输出密码、Token、Cookie、手机号等敏感信息。
- 认证、权限、拦截器、注解相关改动需要兼顾 admin 与 web 两类入口。

## 数据库与实体

- 修改表结构时，同步考虑 Entity、Mapper、DAO、Qo/Vo 以及初始化 SQL。
- 逻辑删除、创建时间、更新时间等公共字段优先遵循 `BaseEntity`、`LogicEntity` 和 MyBatis Plus 自动填充配置。
- 不随意更改已有字段名、表名、主键策略和逻辑删除语义。

## 依赖与工具

- 不新增依赖，除非现有依赖无法合理完成任务。
- 如需新增依赖，先说明原因、用途和影响范围。
- HTTP、缓存、加密、时间、字符串等通用能力优先复用 `util`、`component`、`config` 中已有实现。

## 验证

- Java 代码改动后优先运行：

```bash
mvn -q -DskipTests compile
```

- 涉及测试或高风险业务逻辑时，优先运行相关测试或补充针对性测试。
- 如果无法运行验证命令，需要在回复中说明原因。

## 协作要求

- 修改前先阅读相关上下文，不要只根据文件名猜测实现。
- 工作区可能存在用户未提交改动，不要回滚或覆盖与任务无关的修改。
- 最终回复应简洁说明改了什么、验证了什么，以及仍需用户注意的事项。
