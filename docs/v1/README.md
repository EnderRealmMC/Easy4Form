# Easy4Form v1 Documentation (Deprecated)

> **⚠️ DEPRECATED / 已废弃**
> 
> **English**: This API v1 is based on the old Cumulus interface and is **deprecated**. It is no longer maintained but remains functional. Please consider:
> - **Recommended**: Use the main proxy package which automatically handles compatibility and provides the best experience
> - **Alternative**: Migrate to v2 for new interface features and active maintenance
> 
> **中文**: 此 API v1 基于旧版 Cumulus 接口，已**废弃**。虽然仍可使用但不再维护。请考虑：
> - **推荐**: 使用主代理包，它会自动处理兼容性并提供最佳体验
> - **备选**: 迁移到 v2 以获得新接口功能和积极维护

## Available Documentation

- **[English API Documentation](api.md)** - Complete API reference and examples in English (Deprecated)
- **[中文 API 文档](api_zh.md)** - 完整的API参考文档和中文示例 (已废弃)

## Quick Links

- **[Main Documentation](../../README.md)** | **[主要文档](../../README_ZH.md)**
- **[Main Proxy API Guide](../api/README.md)** | **[主代理 API 指南](../api/README_ZH.md)**
- **[v2 Documentation](../v2/README.md)** | **[v2 文档](../v2/README_ZH.md)**
- **[Migration Guide](../v2/api.md#migration-from-v1)** | **[迁移指南](../v2/api_zh.md#从-v1-迁移)**

## Deprecation Notice

This version is deprecated because:

- **Old Interface**: Based on the old Cumulus interface which is no longer recommended
- **No Maintenance**: No longer receives updates, bug fixes, or new features
- **Compatibility Issues**: May have compatibility issues with newer Minecraft versions
- **Performance**: Less optimized compared to v2

## Migration Recommendations

### Option 1: Use Main Proxy Package (Recommended)

The easiest migration path is to use the main proxy package:

```java
// Old v1 code
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// New proxy code (recommended)
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// The proxy automatically selects the best implementation
Easy4FormAPI.sendSimpleForm(player, "Title", "Content", buttons, response -> {
    // Handle response
});
```

### Option 2: Migrate to v2 Directly

For projects that need specific v2 features:

```java
// Old v1 code
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// New v2 code
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;
```

## Legacy Support

While deprecated, v1 will continue to work for existing projects. However, we strongly recommend migrating to avoid potential future compatibility issues.

## Getting Help

For migration assistance:

1. Check the [v2 Migration Guide](../v2/api.md#migration-from-v1)
2. Review the [v2 Documentation](../v2/README.md)
3. Consider using the main proxy package for automatic compatibility

---

**For new projects, please use [v2](../v2/README.md) or the main proxy package instead.**