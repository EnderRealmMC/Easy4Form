# Easy4Form v1 文档（已废弃）

> **⚠️ 已废弃**
> 
> 此 API v1 基于旧版 Cumulus 接口，已**废弃**。虽然仍可使用但不再维护。请考虑：
> - **推荐**：使用主代理包，它会自动处理兼容性并提供最佳体验
> - **备选**：迁移到 v2 以获得新接口功能和积极维护

## 可用文档

- **[英文 API 文档](api.md)** - 完整的API参考文档和英文示例（已废弃）
- **[中文 API 文档](api_zh.md)** - 完整的API参考文档和中文示例（已废弃）

## 快速链接

- **[主要文档](../README_ZH.md)** | **[Main Documentation](../README.md)**
- **[主代理 API 指南](../api/README_ZH.md)** | **[Main Proxy API Guide](../api/README.md)**
- **[v2 文档](../v2/README_ZH.md)** | **[v2 Documentation](../v2/README.md)**
- **[迁移指南](#从-v1-迁移)** | **[Migration Guide](README.md#migration-from-v1)**

## 废弃通知

此版本被废弃的原因：

- **旧接口**：基于旧版 Cumulus 接口，不再推荐使用
- **无维护**：不再接收更新、错误修复或新功能
- **兼容性问题**：可能与较新的 Minecraft 版本存在兼容性问题
- **性能**：相比 v2 优化程度较低

## 迁移建议

### 选项 1：使用主代理包（推荐）

最简单的迁移路径是使用主代理包：

```java
// 旧的 v1 代码
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// 新的代理代码（推荐）
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// 代理自动选择最佳实现
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    // 处理响应
});
```

### 选项 2：直接迁移到 v2

对于需要特定 v2 功能的项目：

```java
// 旧的 v1 代码
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// 新的 v2 代码
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;
```

## 遗留支持

虽然已废弃，v1 将继续为现有项目工作。但是，我们强烈建议迁移以避免潜在的未来兼容性问题。

## 获取帮助

如需迁移协助：

1. 查看 [v2 迁移指南](../v2/api_zh.md#从-v1-迁移)
2. 查看 [v2 文档](../v2/README_ZH.md)
3. 考虑使用主代理包以获得自动兼容性

---

**对于新项目，请使用 [v2](../v2/README_ZH.md) 或主代理包。**