# Easy4Form Documentation

Welcome to the Easy4Form documentation! Easy4Form is a powerful and easy-to-use library for creating forms for Bedrock Edition players in Minecraft servers.

## Available Documentation

### Version 2 (Recommended for New Projects)

> **⚠️ Note**: While v2 is actively maintained and based on the new Cumulus interface, we recommend using the main proxy package for the best compatibility experience.

- **[v2 Documentation Hub](v2/README.md)** | **[v2 文档中心](v2/README_ZH.md)**
- **[v2 English API Documentation](v2/api.md)**
- **[v2 中文 API 文档](v2/api_zh.md)**

### Version 1 (Deprecated)

> **⚠️ Deprecated**: v1 is based on the old Cumulus interface and is no longer maintained. Use only for legacy projects.

- **[v1 Documentation Hub](v1/README.md)** | **[v1 文档中心](v1/README_ZH.md)**
- **[v1 English API Documentation](v1/api.md)** (Deprecated)
- **[v1 中文 API 文档](v1/api_zh.md)** (Deprecated)

## Recommendation

**For all new projects**, we strongly recommend using the **main proxy package** instead of directly using v1 or v2 packages. The proxy package:

- Automatically handles compatibility between different Cumulus versions
- Provides the best user experience
- Is actively maintained and updated
- Seamlessly switches between v1 and v2 implementations as needed

## Quick Start

### Using the Main Proxy Package (Recommended)

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// The proxy automatically selects the best implementation
Easy4FormAPI.sendSimpleForm(player, "Title", "Content", buttons, response -> {
    // Handle response
});
```

### Direct v2 Usage (Advanced)

```java
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

// Direct v2 usage for specific new interface features
Easy4FormAPI.sendSimpleForm(player, "Title", "Content", buttons, response -> {
    // Handle response
});
```

## Form Types

Easy4Form supports all three form types available in Bedrock Edition:

1. **Simple Forms**: A list of buttons that players can click
2. **Modal Forms**: A dialog with two buttons (Yes/No, Confirm/Cancel, etc.)
3. **Custom Forms**: Forms with various input elements (text fields, toggles, sliders, dropdowns, etc.)

## Language Support

Documentation is available in multiple languages:

- **English**: Complete API documentation and examples
- **中文 (Chinese)**: 完整的API文档和示例

## Quick Links

- **[Main Documentation](README.md)** | **[主要文档](README_ZH.md)**
- **[Main Proxy API Guide](api/README.md)** | **[主代理 API 指南](api/README_ZH.md)**
- **[v2 Documentation](v2/README.md)** | **[v2 文档](v2/README_ZH.md)**
- **[v1 Documentation](v1/README.md)** | **[v1 文档](v1/README_ZH.md)**
- **[Migration Guide](v1/README.md#migration-from-v1)** | **[迁移指南](v1/README_ZH.md#从-v1-迁移)**

## Getting Help

If you need help or have questions:

1. Check the appropriate version documentation above
2. Look at the examples provided in each documentation
3. For migration help, see the [Migration Guide](v2/api.md#migration-from-v1)

## Version History

- **v2**: Based on new Cumulus interface, actively maintained, enhanced features
- **v1**: Based on old Cumulus interface, deprecated, legacy support only

---

**Choose your documentation version above to get started!**