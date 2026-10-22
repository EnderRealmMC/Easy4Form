# Release Notes

## 3.0.0

- 架构重构为“公开门面 + 内部 Provider”，彻底移除反射、`VersionManager` 与 `V2CompatibilityHandler`
- 删除 `api.v1`、`api.v2`，统一由公开的 `api` 门面经 Provider 注册表分发到 `CumulusProvider` 或 `PaperDialogProvider`
- 移除 `Easy4FormAPI.getFloodgatePlayer(Player/UUID)` 与 `api.PlayerUtils`；玩家工具迁移至 `utils.PlayerUtils`
- `CumulusProvider` 使用 Floodgate 与 Cumulus 的最新非废弃 API（`validResultHandler` / `closedOrInvalidResultHandler`）
- 表单关闭响应：简单表单与模态表单返回 `null`，自定义表单返回空 `Map`
- 新增 `Easy4FormAPI.isFormSupported(Player)`：判断玩家是否存在可用的表单 Provider；没有 Provider 时发送方法仍然静默跳过，调用方可用该接口自行兜底
- 配置移除 `api-version`、`migration-warnings`，保留 `detection-mode`、`uuid-prefix`、`debug`
- 新增按平台选择 Provider 的 `providers` 配置：`bedrock-form` 可选 `cumulus` 或 `none`；`java-dialog` 可选 `paper` 或 `none`（默认 `none`）
- 新增基于 Paper 1.21.8 Dialog API 的 `PaperDialogProvider`：Simple / Modal / Custom 映射为 Java 版 Dialog，响应模型与 Cumulus 保持一致
- Java 版 Dialog 的关闭语义：Paper 没有 Dialog 关闭事件，协议也没有客户端到服务端的关闭包；Provider 将 `multi_action` 的 `exit_action` 绑定为自定义点击动作，Escape 与页脚关闭按钮都会触发 `PlayerCustomClickEvent` 并映射为 `Closed`
- Java 版 Dialog 会增加页脚关闭按钮；自定义表单增加提交按钮；按钮图片与输入框占位符无法映射，label 会作为正文显示在输入控件上方
- Java 版 Dialog 仅在 Paper 1.21.8+、Java 21、客户端协议 ≥ 771（Minecraft 1.21.6）时启用；安装 ViaVersion 时通过 ViaAPI 读取真实客户端版本，未安装时直接视为满足（能连接 1.21.8 服务端的客户端协议必然 ≥ 771），不依赖 1.18 中不存在的 `Player#getProtocolVersion()`
- `plugin.yml` 新增 `ViaVersion` 软依赖；新增 `java-dialog-text.submit` / `java-dialog-text.close` 配置项
- 构建改用 Paper 1.21.8 API，编译需要 JDK 21，产物仍以 Java 17 为目标
- Floodgate 改为软依赖：缺失时自动禁用 Cumulus Provider，且不再把任何玩家识别为基岩版玩家（Cumulus 与表单能力随 Floodgate 一同缺失），插件不再因此停用；仅在 Floodgate 已安装但未启用或 API 调用失败时回退到 UUID 前缀检测
- 门面发送改为由 Provider 注册表按玩家能力路由，基岩版与 Java 版分别命中对应 Provider

## 2.0.0

发布

