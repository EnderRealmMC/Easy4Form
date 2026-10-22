# Release Notes

## 3.0.0

3.0.0 是一次彻底重写：对外只保留 `Easy4FormAPI` 门面和三个 Builder，平台差异全部收进内部实现，同时把支持面从「只服务基岩版表单」扩展到「基岩版 + Java 版」。
本节按新增 / 变更 / 移除 / 破坏性变更 / 构建与发布 / 已知限制分类说明。所有不兼容改动都记在这里，主文档（README）不复述升级注意事项。

### 新增

- **Java 版表单能力**：Simple / Modal / Custom 三类表单都能映射为 Java 版 Dialog，响应模型与基岩版完全一致，同一套调用代码在两个平台拿到同样的回调值。
- **Java 版 Dialog 的版本门控**：仅在 Paper 1.21.7+、Java 21、客户端协议 ≥ 771（Minecraft 1.21.6）时启用。Paper 1.21.6 尚无 Dialog API，1.21.7 与 1.21.8 的 Dialog API 仅存在注解与文档差异。安装 ViaVersion 时通过 ViaAPI 读取真实客户端版本；未安装时直接视为满足（能连接 1.21.7 服务端的客户端协议必然 ≥ 771），不依赖 1.18 中并不存在的 `Player#getProtocolVersion()`。
- **Java 版 Dialog 的关闭识别**：Paper 没有 Dialog 关闭事件，协议也没有客户端到服务端的关闭包。实现把 `multi_action` 的 `exit_action` 绑定为自定义点击动作，Escape 与页脚关闭按钮都会触发 `PlayerCustomClickEvent`，统一映射为「已关闭」。
- **按平台开关实现**：新增 `providers` 配置。`bedrock-form` 可选 `cumulus` 或 `none`；`java-dialog` 可选 `paper` 或 `none`（默认 `none`，需要显式开启）。
- **能力探测接口**：`Easy4FormAPI.isFormSupported(Player)`，判断该玩家当前是否能被任一表单实现接管。没有实现时发送方法仍然静默跳过，调用方可用该接口自行兜底。
- **Kotlin DSL**：新增 `api/dsl`，提供 `player.simpleForm` / `modalForm` / `customForm` 扩展函数与尾随 lambda。逐按钮回调、`confirm` / `cancel`、`submitted` / `closed` 全部由既有 Builder 承载，不新增门面之外的任何能力，Java 调用方式保持不变。
  - 关闭语义与门面一致，不引入第二套判定：Simple / Modal 的 `null` 与 Custom 的空 `Map` 统一路由到 `closed`；`submitted` 只在拿到非空响应时触发。
  - 回调声明为 `fun interface`（`FormAction`、`ButtonClick`、`FormSubmit`）而不是 Kotlin 函数类型，避免跨插件类加载器解析出两份 `kotlin.jvm.functions`。`FormValues` 提供 `string` / `boolean` / `float` / `intIndex` 类型化取值，取值失败返回 `null`，不抛异常。
- **Java 版 Dialog 文本配置**：`java-dialog-text.submit` 与 `java-dialog-text.close`，支持 `§` 颜色代码。
- **`plugin.yml` 新增 `ViaVersion` 软依赖**。

### 变更

- **架构**：重构为「公开门面 + 内部实现」，反射、`VersionManager`、`V2CompatibilityHandler` 全部去掉，平台差异不再靠运行时探测。
- **路由**：发送表单时由内部注册表按玩家能力决定走向，基岩版命中 Cumulus 实现，符合版本条件的 Java 版命中 Paper Dialog 实现。
- **Floodgate 改为软依赖**：缺失时自动禁用基岩版实现，并且不再把任何玩家识别为基岩版玩家（Cumulus 与基岩版表单能力随 Floodgate 一同缺失），插件不再因此停用。仅在 Floodgate 已安装但未启用、或其 API 调用失败时，才回退到 UUID 前缀检测。
- **Cumulus 侧改用最新非废弃 API**：Floodgate 与 Cumulus 的调用更新到 `validResultHandler` / `closedOrInvalidResultHandler`。
- **关闭响应统一**：简单表单与模态表单关闭时回调收到 `null`，自定义表单关闭时收到空 `Map`。
- **发送前置检查**：所有发送方法在下发前统一做能力检查，没有可用实现时静默返回，响应处理器不会触发（`createCustomForm` 此时返回 `null`）。

### 移除

- `api.v1`、`api.v2` 两个版本包，以及与之配套的兼容性分发代码。
- `Easy4FormAPI.getFloodgatePlayer(Player)` 与 `Easy4FormAPI.getFloodgatePlayer(UUID)`。
- `api.PlayerUtils`（玩家工具迁移到 `utils.PlayerUtils`）。
- `VersionManager`、`V2CompatibilityHandler`。
- 配置项 `api-version`、`migration-warnings`；`detection-mode`、`uuid-prefix`、`debug` 保持不变。

### 破坏性变更

- **入口包收敛**：`api.v1` / `api.v2` 已删除。所有调用改用 `cn.enderrealm.easy4form.api.Easy4FormAPI` 静态门面，或 `api` 包下的 `SimpleFormBuilder` / `ModalFormBuilder` / `CustomFormBuilder`。
- **Floodgate 玩家对象不再暴露**：`getFloodgatePlayer` 被删除且没有等价替代。库内只需要「是否基岩版」这一判定，请改用 `isBedrockPlayer`；确实需要 Floodgate 原生对象（如取得基岩版 ID）请自行依赖 Floodgate API。
- **`PlayerUtils` 换包**：`cn.enderrealm.easy4form.api.PlayerUtils` → `cn.enderrealm.easy4form.utils.PlayerUtils`，方法签名不变，只需改 import。
- **关闭响应需要判空**：Simple / Modal 的回调参数可能为 `null`，Custom 的 `Map` 可能为空。升级后请检查全部响应处理器，先判空再取值，否则会在玩家关闭表单时抛空指针。
- **Floodgate 缺失不再停用插件**：过去缺少 Floodgate 会直接停用，现在改为禁用基岩版实现后继续运行，同时 `isBedrockPlayer` 对所有玩家返回 `false`。依赖「插件停用」来暴露部署问题的运维方式需要改为主动检查启动日志与 `isBedrockPlayer` 结果。
- **部署产物一分为二**：编译依赖请使用不含 stdlib 的主产物，部署使用 `-all` 产物（详见「构建与发布」）。使用 Kotlin DSL 的插件必须让全服务器只保留一份 kotlin-stdlib 定义，否则 DSL 的 lambda 无法跨插件传递。

### 构建与发布

- 构建改用 Paper 1.21.7 API，编译需要 JDK 21，产物仍以 Java 17 为目标运行。
- 引入 Kotlin 2.4.21 与 `kotlin-stdlib`，编译固定 `-language-version=2.0`，产物 metadata 为 `2.0.0`。实测 Kotlin 1.9.24 与 2.0.21 编译的消费者都能正常调用 DSL。
- 主产物刻意不打包 stdlib：一旦打包，使用 Kotlin 2.4 以下编译器的项目会因 stdlib 的 `2.4.0` metadata 直接编译失败。
- 产物分工：`easy4form-3.0.0.jar` 只含本库类，作为编译依赖坐标；`easy4form-3.0.0-all.jar` 内置 stdlib，放进 `plugins/` 部署。发布流水线上传的是 `-all` 产物，但资产名沿用 `easy4form-3.0.0.jar`，下载方只需取一个文件。
- 部署产物体积约 1.9 MB（此前约 70 KB），增量全部来自内置的 kotlin-stdlib。

### 已知限制

- Java 版 Dialog 无法映射按钮图片与输入框占位符；自定义表单的 label 会作为正文显示在对应输入控件上方。
- Java 版 Dialog 会额外增加页脚关闭按钮，自定义表单会额外增加提交按钮。
- 客户端低于 Minecraft 1.21.6 时 Java 表单不发送；安装 ViaVersion 但读不到真实客户端版本时同样跳过。
- 关闭识别只覆盖会触发 `exit_action` 的路径，即 Escape 与页脚关闭按钮。
- UUID 前缀检测只在 Floodgate 已安装时生效；没有 Floodgate 时没有任何玩家会被判定为基岩版。

## 2.0.0

发布

