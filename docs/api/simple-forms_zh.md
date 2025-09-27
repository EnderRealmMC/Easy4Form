# 简单表单 - Easy4Form 主代理 API

**语言**: [English](simple-forms.md) | [中文](simple-forms_zh.md)

**返回**: [主 API 文档](README_ZH.md)

简单表单是基于按钮的表单，为用户提供可点击选项列表。它们非常适合菜单、导航以及用户需要从多个选项中选择的任何场景。

## 目录

- [概述](#概述)
- [基本用法](#基本用法)
- [高级功能](#高级功能)
- [构建器模式](#构建器模式)
- [最佳实践](#最佳实践)
- [示例](#示例)
- [故障排除](#故障排除)

## 概述

### 什么是简单表单？

简单表单显示：
- 顶部的**标题**
- 描述用途的**内容文本**
- 用户可以点击的**按钮**列表
- 每个按钮在被点击时返回其**索引**（0、1、2等）

### 何时使用简单表单

✅ **完美适用于：**
- 主菜单
- 类别选择
- 功能间导航
- 动作或命令列表
- 服务器选择

❌ **不适用于：**
- 是/否问题（使用[模态表单](modal-forms_zh.md)）
- 数据输入（使用[自定义表单](custom-forms_zh.md)）
- 超过10-12个按钮的表单（用户体验考虑）

## 基本用法

### 简单示例

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import java.util.Arrays;

public void showMainMenu(Player player) {
    List<String> buttons = Arrays.asList(
        "开始游戏",
        "查看统计",
        "设置",
        "退出"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "主菜单",                        // 标题
        "欢迎！请选择一个选项：",          // 内容
        buttons,                        // 按钮列表
        response -> {                   // 响应处理器
            if (response != null) {
                handleMenuSelection(player, response);
            } else {
                player.sendMessage("菜单已取消。");
            }
        }
    );
}

private void handleMenuSelection(Player player, int buttonIndex) {
    switch (buttonIndex) {
        case 0: // 开始游戏
            showGameMenu(player);
            break;
        case 1: // 查看统计
            showPlayerStats(player);
            break;
        case 2: // 设置
            showSettingsMenu(player);
            break;
        case 3: // 退出
            player.sendMessage("感谢游玩！");
            break;
        default:
            player.sendMessage("无效选择。");
    }
}
```

### 响应处理

响应处理器接收一个可能为以下值的 `Integer`：
- **0、1、2等**：被点击按钮的索引
- **null**：表单被关闭/取消

```java
response -> {
    if (response == null) {
        // 表单被关闭或取消
        player.sendMessage("表单已取消。");
        return;
    }
    
    // 处理按钮点击
    switch (response) {
        case 0:
            // 第一个按钮被点击
            break;
        case 1:
            // 第二个按钮被点击
            break;
        // ... 更多情况
    }
}
```

## 高级功能

### 动态按钮生成

```java
public void showPlayerList(Player player) {
    List<String> buttons = new ArrayList<>();
    
    // 将在线玩家添加为按钮
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.equals(player)) {
            buttons.add(onlinePlayer.getName());
        }
    }
    
    // 添加导航按钮
    buttons.add("刷新");
    buttons.add("取消");
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "在线玩家",
        "选择一个玩家进行互动：",
        buttons,
        response -> {
            if (response == null) return;
            
            if (response == buttons.size() - 2) {
                // 刷新按钮
                showPlayerList(player); // 递归调用
            } else if (response == buttons.size() - 1) {
                // 取消按钮
                player.sendMessage("已取消。");
            } else {
                // 玩家被选中
                String selectedPlayerName = buttons.get(response);
                Player selectedPlayer = Bukkit.getPlayer(selectedPlayerName);
                if (selectedPlayer != null) {
                    showPlayerInteractionMenu(player, selectedPlayer);
                }
            }
        }
    );
}
```

### 错误处理

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "标题",
    "内容",
    buttons,
    response -> {
        try {
            if (response != null) {
                handleResponse(player, response);
            }
        } catch (Exception e) {
            player.sendMessage("处理您的选择时发生错误。");
            getLogger().severe("处理表单响应时出错：" + e.getMessage());
        }
    }
);
```

## 构建器模式

对于更复杂的表单，使用构建器模式：

```java
import cn.enderrealm.easy4form.api.SimpleFormBuilder;

public void showAdvancedMenu(Player player) {
    new SimpleFormBuilder()
        .title("高级菜单")
        .content("请仔细选择您的操作：")
        .button("小游戏")
        .button("排行榜")
        .button("商店")
        .button("设置")
        .button("退出")
        .responseHandler(response -> {
            if (response != null) {
                handleAdvancedMenuResponse(player, response);
            }
        })
        .send(player);
}
```

### 构建器方法

```java
SimpleFormBuilder builder = new SimpleFormBuilder()
    .title("表单标题")                      // 设置表单标题
    .content("表单描述")                    // 设置表单内容
    .button("按钮 1")                       // 添加一个按钮
    .button("按钮 2")                       // 添加另一个按钮
    .buttons(Arrays.asList("A", "B", "C"))  // 添加多个按钮
    .responseHandler(response -> { ... })   // 设置响应处理器
    .errorHandler(error -> { ... });       // 设置错误处理器（可选）

// 发送表单
builder.send(player);
```

## 最佳实践

### 1. 保持按钮文本简洁

```java
// 好的
List<String> buttons = Arrays.asList(
    "游戏",
    "统计",
    "设置",
    "退出"
);

// 避免
List<String> buttons = Arrays.asList(
    "点击这里开始游戏",
    "查看您的详细统计信息和成就",
    "打开设置和配置菜单",
    "退出游戏并返回服务器大厅"
);
```

### 2. 提供清晰的导航

```java
// 始终包含导航选项
List<String> buttons = Arrays.asList(
    "选项 1",
    "选项 2",
    "选项 3",
    "返回",      // 返回上一个菜单
    "取消"       // 关闭表单
);
```

### 4. 处理边界情况

```java
response -> {
    if (response == null) {
        // 表单被取消
        return;
    }
    
    if (response < 0 || response >= buttons.size()) {
        // 无效响应（不应该发生，但要安全）
        player.sendMessage("无效选择。");
        return;
    }
    
    // 处理有效响应
    handleValidResponse(player, response);
}
```

### 5. 限制按钮数量

```java
// 好的 - 合理的选项数量
List<String> buttons = Arrays.asList(
    "选项 1", "选项 2", "选项 3", "选项 4", "选项 5"
);

// 对于许多选项考虑分页
if (allOptions.size() > 8) {
    showPaginatedMenu(player, allOptions, 0);
} else {
    showSimpleMenu(player, allOptions);
}
```

## 示例

### 示例 1：服务器选择器

```java
public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList(
        "生存模式",
        "PvP 竞技场",
        "小游戏",
        "创造模式",
        "返回大厅"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "服务器选择",
        "选择要加入的服务器：",
        servers,
        response -> {
            if (response == null) return;
            
            switch (response) {
                case 0: // 生存模式
                    connectToServer(player, "survival");
                    break;
                case 1: // PvP
                    connectToServer(player, "pvp");
                    break;
                case 2: // 小游戏
                    showMiniGameSelector(player);
                    break;
                case 3: // 创造模式
                    connectToServer(player, "creative");
                    break;
                case 4: // 返回
                    showMainMenu(player);
                    break;
            }
        }
    );
}
```

### 示例 2：商店分类

```java
public void showShopCategories(Player player) {
    List<String> categories = Arrays.asList(
        "武器",
        "盔甲",
        "食物",
        "工具",
        "特殊物品",
        "查看余额",
        "退出商店"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "商店",
        String.format("欢迎 %s！您的余额：$%.2f", 
                     player.getName(), getPlayerBalance(player)),
        categories,
        response -> {
            if (response == null) return;
            
            if (response == categories.size() - 2) {
                // 查看余额
                player.sendMessage(String.format("您的余额：$%.2f", 
                                                getPlayerBalance(player)));
                showShopCategories(player); // 再次显示菜单
            } else if (response == categories.size() - 1) {
                // 退出
                player.sendMessage("感谢光临商店！");
            } else {
                // 显示分类物品
                showCategoryItems(player, response);
            }
        }
    );
}
```

### 示例 3：分页列表

```java
public void showPaginatedList(Player player, List<String> allItems, int page) {
    int itemsPerPage = 6;
    int startIndex = page * itemsPerPage;
    int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());
    
    List<String> buttons = new ArrayList<>();
    
    // 为当前页面添加物品
    for (int i = startIndex; i < endIndex; i++) {
        buttons.add(allItems.get(i));
    }
    
    // 添加导航按钮
    if (page > 0) {
        buttons.add("上一页");
    }
    if (endIndex < allItems.size()) {
        buttons.add("下一页");
    }
    buttons.add("关闭");
    
    String title = String.format("物品（第 %d/%d 页）", 
                                page + 1, 
                                (allItems.size() + itemsPerPage - 1) / itemsPerPage);
    
    Easy4FormAPI.sendSimpleForm(
        player,
        title,
        "选择一个物品：",
        buttons,
        response -> {
            if (response == null) return;
            
            int itemCount = endIndex - startIndex;
            
            if (response < itemCount) {
                // 物品被选中
                String selectedItem = allItems.get(startIndex + response);
                handleItemSelection(player, selectedItem);
            } else {
                // 导航按钮
                int navIndex = response - itemCount;
                if (page > 0 && navIndex == 0) {
                    // 上一页
                    showPaginatedList(player, allItems, page - 1);
                } else if (endIndex < allItems.size() && 
                          ((page > 0 && navIndex == 1) || (page == 0 && navIndex == 0))) {
                    // 下一页
                    showPaginatedList(player, allItems, page + 1);
                }
                // 关闭按钮 - 什么都不做，表单自动关闭
            }
        }
    );
}
```

## 故障排除

### 常见问题

**按钮不显示：**
- 确保按钮列表不为空
- 检查所有按钮字符串都不为 null
- 验证玩家是基岩版玩家

**点击了错误的按钮：**
- 记住按钮索引从 0 开始
- 检查您的响应处理逻辑
- 使用调试来记录响应值

**表单不显示：**
- 验证 Easy4FormAPI.isBedrockPlayer(player) 返回 true
- 检查服务器日志中的错误
- 确保 Floodgate 配置正确

### 调试示例

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "调试表单",
    "测试表单显示",
    Arrays.asList("按钮 1", "按钮 2", "按钮 3"),
    response -> {
        // 记录响应以进行调试
        getLogger().info(String.format("玩家 %s 点击了按钮：%s", 
                                      player.getName(), 
                                      response != null ? response.toString() : "null"));
        
        if (response != null) {
            player.sendMessage("您点击了按钮 " + (response + 1));
        } else {
            player.sendMessage("表单被取消");
        }
    }
);
```

## 相关文档

- **[模态表单](modal-forms_zh.md)** - 用于是/否确认
- **[自定义表单](custom-forms_zh.md)** - 用于数据输入
- **[主 API 文档](README_ZH.md)** - 概述和入门
- **[玩家工具](README_ZH.md#玩家工具)** - 辅助方法

---

**下一步：**
- 尝试[模态表单指南](modal-forms_zh.md)了解确认对话框
- 探索[自定义表单](custom-forms_zh.md)进行高级输入收集
- 返回[主 API 文档](README_ZH.md)了解其他功能