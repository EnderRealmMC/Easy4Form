# Easy4Form 主代理 API 文档

> **✅ 推荐使用**
> 
> 这是 Easy4Form 的**主代理 API**，是**推荐的使用方式**。它会自动选择最佳的底层实现（v1 或 v2），提供最佳的兼容性和性能。
> 
> - **自动兼容性**：根据服务器环境自动选择合适的 API 版本
> - **最佳性能**：优先使用 v2 API，在不可用时回退到 v1
> - **简化开发**：无需关心底层实现细节
> - **未来保证**：随着新版本发布自动获得改进

**语言**: [English](README.md) | [中文](README_ZH.md)

**返回**: [主文档](../README_ZH.md)

Easy4Form 主代理 API 是使用 Easy4Form 的**推荐**方式。它提供自动版本检测和兼容性处理，确保您的代码在不同 Cumulus 版本间无缝工作。

## 目录

- [为什么使用主代理 API？](#为什么使用主代理-api)
- [快速开始](#快速开始)
- [表单类型](#表单类型)
  - [简单表单](simple-forms_zh.md)
  - [模态表单](modal-forms_zh.md)
  - [自定义表单](custom-forms_zh.md)
- [玩家工具](#玩家工具)
- [高级功能](#高级功能)
- [迁移指南](#迁移指南)

## 为什么使用主代理 API？

主代理 API 提供了几个关键优势：

### 🔄 **自动版本管理**
- 自动检测可用的 Cumulus 版本
- 将调用路由到适当的实现（v1 或 v2）
- 无需担心兼容性问题

### 🛡️ **面向未来**
- 随着 Easy4Form 的发展，您的代码保持兼容
- 无需代码更改即可无缝升级
- 防止破坏性更改

### 🚀 **最佳性能**
- 使用可用的最优化实现
- 需要时自动回退到稳定版本
- 代理层开销最小

### 📚 **统一 API**
- 所有功能的单一导入
- 跨版本一致的方法签名
- 简化的开发体验

### 🔧 **易于维护**
- 集中式错误处理
- 全面的日志记录和调试
- 积极的支持和更新

## 快速开始

### 安装

将 Easy4Form 添加到您的项目依赖中：

```xml
<dependency>
    <groupId>cn.enderrealm</groupId>
    <artifactId>easy4form</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 基本用法

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import org.bukkit.entity.Player;

// 代理自动处理版本检测和路由
public class MyPlugin {
    
    public void sendWelcomeForm(Player player) {
        List<String> buttons = Arrays.asList("开始游戏", "设置", "退出");
        
        Easy4FormAPI.sendSimpleForm(
            player,
            "欢迎！",
            "请选择一个选项：",
            buttons,
            response -> {
                if (response != null) {
                    switch (response) {
                        case 0: // 开始游戏
                            player.sendMessage("正在启动游戏...");
                            break;
                        case 1: // 设置
                            openSettingsForm(player);
                            break;
                        case 2: // 退出
                            player.kickPlayer("感谢游玩！");
                            break;
                    }
                }
            }
        );
    }
}
```

## 表单类型

Easy4Form 支持三种主要的表单类型，每种都针对不同的使用场景进行了优化：

### 1. 简单表单
非常适合菜单、导航和单选选择。

**📖 [完整简单表单指南 →](simple-forms_zh.md)**

```java
SimpleFormBuilder form = new SimpleFormBuilder()
    .title("主菜单")
    .content("选择一个选项：")
    .addButton("开始游戏", "textures/ui/play_button")
    .addButton("设置", "textures/ui/settings_button")
    .addButton("退出", "textures/ui/exit_button");

Easy4FormAPI.sendSimpleForm(player, form, (response, buttonIndex) -> {
    switch (buttonIndex) {
        case 0: startGame(player); break;
        case 1: showSettings(player); break;
        case 2: player.kickPlayer("感谢游戏！"); break;
    }
});
```

### 2. 模态表单
非常适合是/否确认和二元选择。

**📖 [完整模态表单指南 →](modal-forms_zh.md)**

```java
ModalFormBuilder form = new ModalFormBuilder()
    .title("确认操作")
    .content("您确定要删除您的世界吗？")
    .positiveButton("是，删除")
    .negativeButton("取消");

Easy4FormAPI.sendModalForm(player, form, confirmed -> {
    if (confirmed) {
        deleteWorld(player);
        player.sendMessage("世界删除成功！");
    } else {
        player.sendMessage("操作已取消。");
    }
});
```

### 3. 自定义表单
强大的表单，用于具有多种输入类型的复杂数据收集。

**📖 [完整自定义表单指南 →](custom-forms_zh.md)**

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("玩家设置")
    .addInput("username", "显示名称：", "输入您的姓名")
    .addToggle("notifications", "启用通知", true)
    .addDropdown("difficulty", "难度：", 
                Arrays.asList("简单", "普通", "困难"), 1)
    .addSlider("volume", "音量：", 0, 100, 5, 75);

Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response != null) {
        String username = response.getInput("username");
        boolean notifications = response.getToggle("notifications");
        String difficulty = response.getDropdown("difficulty");
        double volume = response.getSlider("volume");
        
        // 应用设置
        applyPlayerSettings(player, username, notifications, difficulty, volume);
    }
});
```

## 玩家工具

代理 API 包含有用的玩家管理工具：

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// 检查玩家是否为基岩版玩家
if (Easy4FormAPI.isBedrockPlayer(player)) {
    // 向基岩版玩家发送表单
    Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
} else {
    // 对 Java 版玩家进行不同处理
    player.sendMessage("此功能仅适用于基岩版玩家。");
}

// 获取 Floodgate 玩家实例
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
if (floodgatePlayer != null) {
    // 访问 Floodgate 特定功能
    String deviceName = floodgatePlayer.getDeviceOs().toString();
    player.sendMessage("正在使用：" + deviceName);
}
```

## 高级功能

### 错误处理

代理 API 提供全面的错误处理：

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "标题",
    "内容",
    buttons,
    response -> {
        // 处理成功响应
        if (response != null) {
            handleButtonClick(response);
        } else {
            // 表单被关闭或取消
            player.sendMessage("表单已取消。");
        }
    },
    error -> {
        // 处理错误（可选的错误处理器）
        getLogger().warning("表单错误：" + error.getMessage());
        player.sendMessage("显示表单时发生错误。");
    }
);
```

### 配置

通过 `config.yml` 配置文件自定义代理行为：

```yaml
# Easy4Form Configuration
# Easy4Form 配置文件

# Detection mode for Bedrock players
# 基岩版玩家检测模式
# Options: floodgate, uuid
detection-mode: floodgate

# UUID prefix for Bedrock players (only used when detection-mode is 'uuid')
# 基岩版玩家的UUID前缀（仅在detection-mode为'uuid'时使用）
uuid-prefix: "00000000-0000"

# Debug mode
# 调试模式
debug: false

# API version (v1 and v2 are supported, v2 is recommended)
# API版本（支持v1和v2，推荐使用v2）
api-version: "v2"

# Enable migration warnings for deprecated API usage
# 启用已弃用API使用的迁移警告
migration-warnings: true
```

> **注意**：
> - 配置选项由 `config.yml` 文件决定，请根据您的需求调整
> - 随着版本更新，建议及时更新配置以获得更好的处理和实现
> - 文档中的部分代码示例可能涉及伪代码或未实现的具体功能，具体功能需要自己实现

## 版本差异说明

### v1 与 v2 响应处理差异

**重要**：v1 和 v2 在表单关闭时的响应处理存在差异：

- **v1 API**：当表单关闭时，SimpleForm 响应处理器接收 `null` 值
- **v2 API**：当表单关闭时，SimpleForm 响应处理器接收 `-1` 值

```java
// v1 响应处理
response -> {
    if (response == null) {
        // 表单被关闭
        player.sendMessage("表单已取消");
    } else {
        // 处理按钮点击
        handleButtonClick(player, response);
    }
}

// v2 响应处理
.validResultHandler(response -> {
    // 仅处理有效的按钮点击
    handleButtonClick(player, response);
})
.closedOrInvalidResultHandler(() -> {
    // 处理表单关闭
    player.sendMessage("表单已取消");
})
```

### API 版本推荐

- **推荐使用**：主代理 API（自动选择最佳版本）
- **v2**：基于新版 Cumulus 接口，性能更好，功能更丰富
- **v1**：已废弃，仅用于兼容性

## 迁移指南

### 从 v1 API 迁移

```java
// 旧的 v1 代码
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// 新的代理代码（推荐）
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// 方法调用保持不变！
Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
```

### 从 v2 API 迁移

```java
// 旧的 v2 代码
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

// 新的代理代码（推荐）
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// 方法调用保持不变！
Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
```

### 迁移的好处

1. **未来兼容性**：您的代码将与未来的 Easy4Form 版本兼容
2. **自动优化**：始终使用最佳可用实现
3. **简化维护**：无需跟踪版本兼容性
4. **增强功能**：访问代理特定功能，如错误处理

## 最佳实践

### 1. 始终检查基岩版玩家

```java
if (Easy4FormAPI.isBedrockPlayer(player)) {
    // 发送表单
} else {
    // 为 Java 版玩家提供替代方案
}
```

### 2. 处理空响应

```java
response -> {
    if (response != null) {
        // 处理响应
    } else {
        // 表单被取消
    }
}
```

### 3. 使用描述性标题和内容

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "服务器菜单",  // 清晰、描述性的标题
    "请选择您想要做的事情：",  // 有用的内容
    buttons,
    handler
);
```

### 4. 提供反馈

```java
response -> {
    if (response != null) {
        player.sendMessage("正在处理您的选择...");
        // 处理响应
    }
}
```

## 故障排除

### 常见问题

**表单未显示：**
- 确保玩家是基岩版玩家
- 检查 Floodgate 是否正确安装
- 验证 Easy4Form 是否已启用

**空响应：**
- 玩家关闭了表单
- 网络问题
- 表单超时

**导入错误：**
- 使用 `cn.enderrealm.easy4form.api.Easy4FormAPI`
- 确保 Easy4Form 在您的依赖中

### 调试模式

启用调试日志进行故障排除：

```java
Easy4FormAPI.setDebugMode(true);
```

## 支持

获取帮助和支持：

1. 查看[详细表单指南](simple-forms_zh.md)
2. 查看[故障排除部分](#故障排除)
3. 启用调试模式获取详细日志
4. 查看代码仓库

---

**准备开始了吗？** 选择您的表单类型：
- 📋 **[简单表单](simple-forms_zh.md)** - 完美适用于菜单和选择
- ❓ **[模态表单](modal-forms_zh.md)** - 非常适合确认
- 📝 **[自定义表单](custom-forms_zh.md)** - 高级输入收集