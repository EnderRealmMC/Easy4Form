# Release Notes

## 3.0.0

- 架构重构为“公开门面 + 内部 Provider”，彻底移除反射、`VersionManager` 与 `V2CompatibilityHandler`
- 删除 `api.v1`、`api.v2`，统一由公开的 `api` 门面调用 `CumulusProvider`
- 移除 `Easy4FormAPI.getFloodgatePlayer(Player/UUID)` 与 `api.PlayerUtils`；玩家工具迁移至 `utils.PlayerUtils`
- `CumulusProvider` 使用 Floodgate 与 Cumulus 的最新非废弃 API（`validResultHandler` / `closedOrInvalidResultHandler`）
- 表单关闭响应：简单表单与模态表单返回 `null`，自定义表单返回空 `Map`
- 配置移除 `api-version`、`migration-warnings`，保留 `detection-mode`、`uuid-prefix`、`debug`

## 2.0.0

发布

