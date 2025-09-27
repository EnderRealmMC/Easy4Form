# 模态表单 - Easy4Form 主代理 API

**语言**: [English](modal-forms.md) | [中文](modal-forms_zh.md)

**返回**: [主 API 文档](README_ZH.md)

模态表单是对话框样式的表单，为用户提供两个选择，通常是"是/否"、"确认/取消"或类似的二元决策。它们非常适合确认、警告以及任何需要简单二元选择的场景。

## 目录

- [概述](#概述)
- [基本用法](#基本用法)
- [高级功能](#高级功能)
- [构建器模式](#构建器模式)
- [最佳实践](#最佳实践)
- [示例](#示例)
- [故障排除](#故障排除)

## 概述

### 什么是模态表单？

模态表单显示：
- 顶部的**标题**
- 解释情况或问题的**内容文本**
- **两个按钮**：通常代表"是/否"、"确认/取消"等
- 返回**布尔值**：第一个按钮返回 `true`，第二个按钮返回 `false`

### 何时使用模态表单

✅ **适用于：**
- 确认对话框（"您确定吗？"）
- 是/否问题
- 接受/拒绝提示
- 警告确认
- 二元选择

❌ **不适用于：**
- 多选选择（使用[简单表单](simple-forms_zh.md)）
- 数据输入（使用[自定义表单](custom-forms_zh.md)）
- 导航菜单（使用[简单表单](simple-forms_zh.md)）

## 基本用法

### 简单示例

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

public void confirmPlayerKick(Player admin, Player target) {
    Easy4FormAPI.sendModalForm(
        admin,
        "确认操作",                                    // 标题
        String.format("您确定要踢出 %s 吗？",              // 内容
                     target.getName()),
        "是的，踢出",                                  // True 按钮（第一个按钮）
        "取消",                                       // False 按钮（第二个按钮）
        response -> {                                 // 响应处理器
            if (response != null) {
                if (response) {
                    // 用户点击了"是的，踢出"（true）
                    target.kickPlayer("您已被 " + admin.getName() + " 踢出");
                    admin.sendMessage("玩家 " + target.getName() + " 已被踢出。");
                } else {
                    // 用户点击了"取消"（false）
                    admin.sendMessage("踢出已取消。");
                }
            } else {
                // 表单被关闭而没有点击任何按钮
                admin.sendMessage("操作已取消。");
            }
        }
    );
}
```

### 响应处理

响应处理器接收一个 `Boolean`，可能是：
- **true**：第一个按钮（通常是"是"、"确认"、"接受"）被点击
- **false**：第二个按钮（通常是"否"、"取消"、"拒绝"）被点击
- **null**：表单被关闭而没有点击任何按钮

```java
response -> {
    if (response == null) {
        // 表单被关闭而没有选择
        player.sendMessage("没有做出选择。");
        return;
    }
    
    if (response) {
        // 第一个按钮被点击（true）
        handleConfirmation(player);
    } else {
        // 第二个按钮被点击（false）
        handleCancellation(player);
    }
}
```

## 高级功能

### 动态内容

```java
public void confirmPurchase(Player player, String itemName, double price) {
    double playerBalance = getPlayerBalance(player);
    
    String content;
    String confirmButton;
    
    if (playerBalance >= price) {
        content = String.format(
            "购买 %s，价格 $%.2f？\n\n您的余额：$%.2f\n购买后余额：$%.2f",
            itemName, price, playerBalance, playerBalance - price
        );
        confirmButton = "立即购买";
    } else {
        content = String.format(
            "余额不足！\n\n%s 价格 $%.2f\n您的余额：$%.2f\n还需要 $%.2f",
            itemName, price, playerBalance, price - playerBalance
        );
        confirmButton = "确定";
    }
    
    Easy4FormAPI.sendModalForm(
        player,
        "购买确认",
        content,
        confirmButton,
        "取消",
        response -> {
            if (response != null && response && playerBalance >= price) {
                // 处理购买
                processPurchase(player, itemName, price);
            } else if (response != null && !response) {
                player.sendMessage("购买已取消。");
            }
        }
    );
}
```

### 错误处理与重试

```java
public void confirmDangerousAction(Player player, String action, int attempts) {
    if (attempts > 3) {
        player.sendMessage("尝试次数过多。为了安全，操作已取消。");
        return;
    }
    
    Easy4FormAPI.sendModalForm(
        player,
        "危险操作",
        String.format(
            "警告：此操作无法撤销！\n\n" +
            "操作：%s\n\n" +
            "您确定要继续吗？\n" +
            "（第 %d/3 次尝试）",
            action, attempts
        ),
        "是的，我确定",
        "取消",
        response -> {
            try {
                if (response == null) {
                    player.sendMessage("操作已取消。");
                } else if (response) {
                    // 执行危险操作
                    performDangerousAction(player, action);
                } else {
                    player.sendMessage("操作已取消。");
                }
            } catch (Exception e) {
                player.sendMessage("发生错误。请重试。");
                getLogger().severe("危险操作错误：" + e.getMessage());
                
                // 增加尝试计数器后重试
                confirmDangerousAction(player, action, attempts + 1);
            }
        }
    );
}
```

## 构建器模式

对于更复杂的模态表单，使用构建器模式：

```java
import cn.enderrealm.easy4form.api.ModalFormBuilder;

public void showAdvancedConfirmation(Player player) {
    new ModalFormBuilder()
        .title("高级确认")
        .content("这是一个需要仔细考虑的复杂决定。")
        .trueButton("我接受")
        .falseButton("我拒绝")
        .responseHandler(response -> {
            if (response != null) {
                handleAdvancedResponse(player, response);
            }
        })
        .errorHandler(error -> {
            player.sendMessage("发生错误：" + error.getMessage());
        })
        .send(player);
}
```

### 构建器方法

```java
ModalFormBuilder builder = new ModalFormBuilder()
    .title("表单标题")                         // 设置表单标题
    .content("表单内容和问题")                  // 设置表单内容
    .trueButton("是")                         // 设置第一个按钮文本（返回 true）
    .falseButton("否")                        // 设置第二个按钮文本（返回 false）
    .responseHandler(response -> { ... })     // 设置响应处理器
    .errorHandler(error -> { ... });         // 设置错误处理器（可选）

// 发送表单
builder.send(player);
```

## 最佳实践

### 1. 使用清晰、面向操作的按钮文本

```java
// 好 - 清晰且具体
Easy4FormAPI.sendModalForm(
    player,
    "删除物品",
    "您确定要删除此物品吗？此操作无法撤销。",
    "永久删除",      // 明确后果
    "保留物品",      // 明确替代选择
    handler
);

// 避免 - 通用且不清楚
Easy4FormAPI.sendModalForm(
    player,
    "确认",
    "您要继续吗？",
    "是",           // 不清楚"是"意味着什么
    "否",           // 不清楚"否"意味着什么
    handler
);
```

### 2. 在内容中提供上下文

```java
// 好 - 提供完整上下文
String content = String.format(
    "您即将传送到 %s。\n\n" +
    "这将花费 $%.2f 并有 10 秒冷却时间。\n\n" +
    "继续传送？",
    destination, cost
);

// 避免 - 缺乏上下文
String content = "传送？";
```

### 3. 处理所有响应情况

```java
response -> {
    if (response == null) {
        // 始终处理表单关闭
        player.sendMessage("操作已取消。");
    } else if (response) {
        // 处理确认
        confirmAction(player);
    } else {
        // 处理取消
        cancelAction(player);
    }
}
```

### 4. 使用适当的标题

```java
// 好 - 描述性标题
"确认购买"
"删除警告"
"传送请求"
"需要权限"

// 避免 - 通用标题
"确认"
"问题"
"对话框"
"表单"
```

### 5. 考虑后果

```java
// 对于不可逆操作，要明确说明
Easy4FormAPI.sendModalForm(
    player,
    "永久操作",
    "警告：此操作无法撤销！\n\n" +
    "您的所有进度都将丢失。\n\n" +
    "您绝对确定吗？",
    "是的，删除所有内容",
    "否，保留我的进度",
    handler
);
```

## 示例

### 示例 1：服务器规则接受

```java
public void showRulesAcceptance(Player player) {
    String rules = 
        "服务器规则：\n\n" +
        "1. 不得破坏或摧毁他人建筑\n" +
        "2. 尊重所有玩家\n" +
        "3. 不得作弊或利用漏洞\n" +
        "4. 遵循管理员指示\n\n" +
        "点击'我接受'即表示您同意遵守这些规则。";
    
    Easy4FormAPI.sendModalForm(
        player,
        "服务器规则",
        rules,
        "我接受",
        "我拒绝",
        response -> {
            if (response == null) {
                // 玩家关闭表单 - 延迟后再次询问
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    showRulesAcceptance(player);
                }, 100L); // 5秒
            } else if (response) {
                // 规则已接受
                setPlayerRulesAccepted(player, true);
                player.sendMessage("欢迎来到服务器！祝您游戏愉快。");
                teleportToSpawn(player);
            } else {
                // 规则被拒绝
                player.kickPlayer("您必须接受服务器规则才能游戏。");
            }
        }
    );
}
```

### 示例 2：经济交易

```java
public void confirmTransaction(Player sender, Player receiver, double amount) {
    double senderBalance = getPlayerBalance(sender);
    
    if (senderBalance < amount) {
        sender.sendMessage("余额不足！");
        return;
    }
    
    String content = String.format(
        "向 %s 发送 $%.2f？\n\n" +
        "您当前余额：$%.2f\n" +
        "交易后余额：$%.2f\n\n" +
        "此操作无法撤销。",
        receiver.getName(), amount, senderBalance, senderBalance - amount
    );
    
    Easy4FormAPI.sendModalForm(
        sender,
        "确认付款",
        content,
        "发送金钱",
        "取消",
        response -> {
            if (response != null && response) {
                // 双重检查余额（以防发生变化）
                if (getPlayerBalance(sender) >= amount) {
                    // 处理交易
                    removePlayerBalance(sender, amount);
                    addPlayerBalance(receiver, amount);
                    
                    sender.sendMessage(String.format("已向 %s 发送 $%.2f", receiver.getName(), amount));
                    receiver.sendMessage(String.format("从 %s 收到 $%.2f", sender.getName(), amount));
                    
                    // 记录交易
                    logTransaction(sender, receiver, amount);
                } else {
                    sender.sendMessage("交易失败：余额不足。");
                }
            } else if (response != null) {
                sender.sendMessage("交易已取消。");
            }
        }
    );
}
```

### 示例 3：PvP 挑战

```java
public void sendPvPChallenge(Player challenger, Player target) {
    // 首先，让挑战者确认
    Easy4FormAPI.sendModalForm(
        challenger,
        "PvP 挑战",
        String.format("挑战 %s 进行 PvP 决斗？\n\n双方玩家都将被传送到竞技场。", 
                     target.getName()),
        "发送挑战",
        "取消",
        response -> {
            if (response != null && response) {
                // 向目标发送挑战
                sendChallengeToTarget(challenger, target);
            } else if (response != null) {
                challenger.sendMessage("挑战已取消。");
            }
        }
    );
}

private void sendChallengeToTarget(Player challenger, Player target) {
    Easy4FormAPI.sendModalForm(
        target,
        "收到 PvP 挑战",
        String.format("%s 向您发起了 PvP 决斗挑战！\n\n" +
                     "您接受这个挑战吗？", challenger.getName()),
        "接受决斗",
        "拒绝",
        response -> {
            if (response != null && response) {
                // 挑战被接受
                challenger.sendMessage(target.getName() + " 接受了您的挑战！");
                target.sendMessage("挑战已接受！正在准备竞技场...");
                startPvPDuel(challenger, target);
            } else if (response != null) {
                // 挑战被拒绝
                challenger.sendMessage(target.getName() + " 拒绝了您的挑战。");
                target.sendMessage("挑战已拒绝。");
            } else {
                // 没有响应
                challenger.sendMessage(target.getName() + " 没有回应您的挑战。");
            }
        }
    );
}
```

### 示例 4：管理员确认

```java
public void confirmAdminAction(Player admin, String action, Runnable actionToPerform) {
    // 检查玩家是否有管理员权限
    if (!admin.hasPermission("server.admin")) {
        admin.sendMessage("您没有此操作的权限。");
        return;
    }
    
    Easy4FormAPI.sendModalForm(
        admin,
        "需要管理员操作",
        String.format(
            "需要管理员确认\n\n" +
            "操作：%s\n\n" +
            "此操作将影响服务器和/或玩家。\n" +
            "请确认您要继续。\n\n" +
            "记录为：%s",
            action, admin.getName()
        ),
        "确认并执行",
        "取消",
        response -> {
            if (response != null && response) {
                try {
                    // 记录管理员操作
                    getLogger().info(String.format("管理员 %s 执行了：%s", admin.getName(), action));
                    
                    // 执行操作
                    actionToPerform.run();
                    
                    admin.sendMessage("管理员操作执行成功。");
                } catch (Exception e) {
                    admin.sendMessage("执行管理员操作时出错：" + e.getMessage());
                    getLogger().severe("管理员操作失败：" + e.getMessage());
                }
            } else if (response != null) {
                admin.sendMessage("管理员操作已取消。");
            }
        }
    );
}

// 使用示例
public void clearAllPlayerInventories(Player admin) {
    confirmAdminAction(admin, "清空所有玩家背包", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.sendMessage("您的背包已被管理员清空。");
        }
    });
}
```

## 故障排除

### 常见问题

**表单不显示：**
- 确保玩家是基岩版玩家
- 检查 Floodgate 是否正确安装
- 验证 Easy4Form 是否已启用

**意外的响应值：**
- 记住：`true` = 第一个按钮，`false` = 第二个按钮，`null` = 关闭
- 检查响应处理器中的布尔逻辑
- 使用调试来记录响应值

**按钮文本显示不正确：**
- 确保按钮文本不为 null 或空
- 检查可能无法正确显示的特殊字符
- 保持按钮文本合理简短

### 调试示例

```java
Easy4FormAPI.sendModalForm(
    player,
    "调试模态",
    "这是一个测试模态表单。",
    "True 按钮",
    "False 按钮",
    response -> {
        // 记录响应以进行调试
        getLogger().info(String.format("玩家 %s 模态响应：%s", 
                                      player.getName(), 
                                      response != null ? response.toString() : "null"));
        
        if (response == null) {
            player.sendMessage("表单被关闭");
        } else if (response) {
            player.sendMessage("您点击了 TRUE 按钮");
        } else {
            player.sendMessage("您点击了 FALSE 按钮");
        }
    }
);
```

## 相关文档

- **[简单表单](simple-forms_zh.md)** - 用于多选选择
- **[自定义表单](custom-forms_zh.md)** - 用于数据输入
- **[主 API 文档](README_ZH.md)** - 概述和入门
- **[玩家工具](README_ZH.md#玩家工具)** - 辅助方法

---

**下一步：**
- 尝试[简单表单指南](simple-forms_zh.md)来创建菜单
- 探索[自定义表单](custom-forms_zh.md)进行高级输入收集
- 返回[主 API 文档](README_ZH.md)了解其他功能