# Release Notes

## 3.0.0

- 架构重构为“公开门面 + 内部 Provider”，彻底移除反射、`VersionManager` 与 `V2CompatibilityHandler`
- 删除 `api.v1`、`api.v2`，统一由公开的 `api` 门面调用 `CumulusProvider`
- 移除 `Easy4FormAPI.getFloodgatePlayer(Player/UUID)` 与 `api.PlayerUtils`；玩家工具迁移至 `utils.PlayerUtils`
- `CumulusProvider` 使用 Floodgate 与 Cumulus 的最新非废弃 API（`validResultHandler` / `closedOrInvalidResultHandler`）
- 表单关闭响应：简单表单与模态表单返回 `null`，自定义表单返回空 `Map`
- 配置移除 `api-version`、`migration-warnings`，保留 `detection-mode`、`uuid-prefix`、`debug`
- 新增按平台选择 Provider 的 `providers` 配置：`bedrock-form` 可选 `cumulus` 或 `none`；`java-dialog` 为未来的 Java 版 Dialog 预留
- Floodgate 改为软依赖：缺失时自动禁用 Cumulus Provider，玩家检测回退到 UUID 前缀，插件不再因此停用
- 门面发送改为由 Provider 注册表按玩家能力路由，为 Java 版表单接入预留空间

## 2.0.0

发布

