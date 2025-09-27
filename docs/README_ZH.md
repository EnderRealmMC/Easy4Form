# Easy4Form 文档

欢迎来到 Easy4Form 文档！Easy4Form 是一个功能强大且易于使用的库，用于在 Minecraft 服务器中为基岩版玩家创建表单。

## 可用文档

### 版本 2（推荐用于新项目）

> **⚠️ 注意**：虽然 v2 正在积极维护并基于新的 Cumulus 接口，但我们建议使用主代理包以获得最佳兼容性体验。

- **[v2 文档中心](v2/README_ZH.md)**
- **[v2 英文 API 文档](v2/api.md)**
- **[v2 中文 API 文档](v2/api_zh.md)**

### 版本 1（已废弃）

> **⚠️ 已废弃**：v1 基于旧的 Cumulus 接口，不再维护。仅用于遗留项目。

- **[v1 文档中心](v1/README_ZH.md)**
- **[v1 英文 API 文档](v1/api.md)**（已废弃）
- **[v1 中文 API 文档](v1/api_zh.md)**（已废弃）

## 推荐

**对于所有新项目**，我们强烈建议使用**主代理包**而不是直接使用 v1 或 v2 包。代理包：

- 自动处理不同 Cumulus 版本之间的兼容性
- 提供最佳用户体验
- 积极维护和更新
- 根据需要无缝切换 v1 和 v2 实现

## 快速开始

### 使用主代理包（推荐）

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// 代理自动选择最佳实现
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    // 处理响应
});
```

### 直接使用 v2（高级用法）

```java
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

// 直接使用 v2 以获得特定的新接口功能
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    // 处理响应
});
```

## 表单类型

Easy4Form 支持基岩版中可用的所有三种表单类型：

1. **简单表单**：玩家可以点击的按钮列表
2. **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
3. **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

## 语言支持

文档提供多种语言版本：

- **English**：完整的 API 文档和示例
- **中文**：完整的 API 文档和示例

## 快速链接

- **[主要文档](README.md)** | **[Main Documentation](README_ZH.md)**
- **[主代理 API 指南](api/README_ZH.md)** | **[Main Proxy API Guide](api/README.md)**
- **[v2 文档](v2/README_ZH.md)** | **[v2 Documentation](v2/README.md)**
- **[v1 文档](v1/README_ZH.md)** | **[v1 Documentation](v1/README.md)**
- **[迁移指南](v1/README_ZH.md#从-v1-迁移)** | **[Migration Guide](v1/README.md#migration-from-v1)**

## 获取帮助

如果您需要帮助或有疑问：

1. 查看上面相应的版本文档
2. 查看每个文档中提供的示例
3. 如需迁移帮助，请参阅[迁移指南](v2/api_zh.md#从-v1-迁移)

## 版本历史

- **v2**：基于新的 Cumulus 接口，积极维护，增强功能
- **v1**：基于旧的 Cumulus 接口，已废弃，仅提供遗留支持

---

**选择上面的文档版本开始使用！**