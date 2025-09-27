# Easy4Form API 文档 v2

> **⚠️ 不推荐使用 / NOT RECOMMENDED**
> 
> **中文**: 虽然此 API v2 基于新版 Cumulus 接口且正在积极维护，但**不推荐**直接使用。请考虑：
> - **推荐**: 使用主代理包，它会自动处理兼容性并提供最佳体验
> - **备选**: 仅在特别需要新接口功能时才直接使用此 v2 包
> 
> **English**: While this API v2 is based on the new Cumulus interface and is actively maintained, it is **not recommended** for direct use. Please consider:
> - **Recommended**: Use the main proxy package which automatically handles compatibility and provides the best experience
> - **Alternative**: Use this v2 package directly only if you specifically need the new interface features

**语言**: [English](api.md) | [中文](api_zh.md)

**返回**: [Main Page](../../README.md) | [主页](../../README_ZH.md)

Easy4Form v2 是基于新版 Cumulus 接口的现代化 Form API，为基岩版玩家创建表单提供了增强的功能和更好的性能。

## 目录

- [简介](#简介)
- [玩家工具](#玩家工具)
- [简单表单](#简单表单)
- [模态表单](#模态表单)
- [自定义表单](#自定义表单)
- [从 v1 迁移](#从-v1-迁移)

## 简介

Easy4Form v2 提供了一个更新的接口，通过新的 Cumulus API 为基岩版玩家创建和发送表单。它支持基岩版中可用的所有三种表单类型，并具有改进的性能和新功能：

- **简单表单**：玩家可以点击的按钮列表
- **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
- **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

### v2 的主要改进

- **新版 Cumulus 接口**：基于最新的 Cumulus API 构建，性能更佳
- **增强的响应处理**：更灵活的响应处理，为不同场景提供单独的处理器
- **改进的错误处理**：更好的错误管理和调试功能
- **现代 Java 特性**：利用现代 Java 特性和 lambda 表达式
- **更好的类型安全**：增强的类型安全和空值处理

## 玩家工具

### 检查玩家是否为基岩版玩家

**方法签名：**
```java
public static boolean isBedrockPlayer(Player player)
```

**参数：**
- `player` (Player) - 要检查的Bukkit玩家对象

**返回值：**
- `boolean` - 如果是基岩版玩家返回`true`，否则返回`false`

**用法：**
```java
boolean isBedrockPlayer = Easy4FormAPI.isBedrockPlayer(player);
```

**示例：**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

public void onPlayerJoin(Player player) {
    if (Easy4FormAPI.isBedrockPlayer(player)) {
        player.sendMessage("欢迎，基岩版玩家！");
    } else {
        player.sendMessage("欢迎，Java版玩家！");
    }
}
```

### 获取 FloodgatePlayer 实例

**方法签名：**
```java
public static FloodgatePlayer getFloodgatePlayer(Player player)
```

**参数：**
- `player` (Player) - 要获取FloodgatePlayer的Bukkit玩家对象

**返回值：**
- `FloodgatePlayer` - FloodgatePlayer实例，如果不是基岩版玩家则返回`null`

**用法：**
```java
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
```

**示例：**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public void getPlayerInfo(Player player) {
    FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
    if (floodgatePlayer != null) {
        String deviceOs = floodgatePlayer.getDeviceOs().toString();
        player.sendMessage("您正在使用：" + deviceOs);
    }
}
```

## 简单表单

简单表单由标题、内容和按钮列表组成。

### 使用 Easy4FormAPI

**方法签名：**
```java
public static void sendSimpleForm(Player player, String title, String content, List<String> buttons, Consumer<Integer> responseHandler)
```

**参数：**
- `player` (Player) - 接收表单的Bukkit玩家
- `title` (String) - 表单标题
- `content` (String) - 表单内容/描述文本
- `buttons` (List<String>) - 按钮文本列表
- `responseHandler` (Consumer<Integer>) - 回调函数，接收按钮索引（从0开始）或表单关闭时为`null`

**用法：**
```java
List<String> buttons = Arrays.asList("按钮 1", "按钮 2", "按钮 3");
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    if (response != null) {
        player.sendMessage("您点击了按钮：" + buttons.get(response));
    } else {
        player.sendMessage("您关闭了表单");
    }
});
```

**示例：服务器选择器**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

import java.util.Arrays;
import java.util.List;

public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList("生存", "创造", "小游戏", "空岛");

    Easy4FormAPI.sendSimpleForm(
            player,
            "服务器选择器",
            "选择要连接的服务器：",
            servers,
            response -> {
                if (response != null) {
                    String server = servers.get(response);
                    player.sendMessage("正在连接到 " + server + "...");
                    // 连接玩家到选定服务器的代码
                }
            }
    );
}
```

### 使用 SimpleFormBuilder

**可用方法：**
- `title(String title)` - 设置表单标题
- `content(String content)` - 设置表单内容/描述
- `button(String text)` - 添加无图片的按钮
- `button(String text, String imagePath)` - 添加带资源包图片的按钮
- `buttonWithUrl(String text, String imageUrl)` - 添加带URL图片的按钮
- `validResultHandler(Consumer<Integer> handler)` - 设置有效响应的处理器
- `closedOrInvalidResultHandler(Runnable handler)` - 设置关闭或无效表单的处理器
- `send(Player player)` - 向玩家发送表单

**方法参数：**
- `title` (String) - 显示在表单顶部的标题文本
- `content` (String) - 显示在标题下方的描述文本
- `text` (String) - 按钮文本
- `imagePath` (String) - 资源包路径（例如："textures/items/diamond"）
- `imageUrl` (String) - 图片的网络URL（例如："https://example.com/image.png"）
- `handler` (Consumer<Integer>) - 接收按钮索引（从0开始）的回调
- `handler` (Runnable) - 关闭/无效表单的回调
- `player` (Player) - 接收表单的Bukkit玩家

**用法：**
```java
new SimpleFormBuilder()
    .title("标题")
    .content("内容")
    .button("按钮 1")                                     // 无图片按钮
    .button("按钮 2", "textures/items/diamond")          // 带资源包图片的按钮
    .buttonWithUrl("按钮 3", "https://example.com/image.png") // 带URL图片的按钮
    .validResultHandler(response -> {
        player.sendMessage("您点击了按钮：" + response);
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("表单被关闭或无效");
    })
    .send(player);
```

**示例：商店分类**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.SimpleFormBuilder;

public void showShopCategories(Player player) {
    new SimpleFormBuilder()
            .title("商店")
            .content("选择一个分类：")
            .button("武器")
            .button("盔甲")
            .button("工具")
            .button("食物")
            .button("杂项")
            .validResultHandler(response -> {
                switch (response) {
                    case 0:
                        openWeaponsShop(player);
                        break;
                    case 1:
                        openArmorShop(player);
                        break;
                    case 2:
                        openToolsShop(player);
                        break;
                    case 3:
                        openFoodShop(player);
                        break;
                    case 4:
                        openMiscShop(player);
                        break;
                }
            })
            .closedOrInvalidResultHandler(() -> {
                // 处理表单关闭
            })
            .send(player);
}
```

## 模态表单

模态表单由标题、内容和两个按钮组成（通常用于是/否或确认/取消场景）。

### 使用 Easy4FormAPI

**方法签名：**
```java
public static void sendModalForm(Player player, String title, String content, String button1, String button2, Consumer<Boolean> responseHandler)
```

**参数：**
- `player` (Player) - 接收表单的Bukkit玩家
- `title` (String) - 表单标题
- `content` (String) - 表单内容/描述文本
- `button1` (String) - 第一个按钮的文本（通常是"是"、"确认"等）
- `button2` (String) - 第二个按钮的文本（通常是"否"、"取消"等）
- `responseHandler` (Consumer<Boolean>) - 回调函数，button1返回`true`，button2返回`false`，关闭表单返回`null`

**用法：**
```java
Easy4FormAPI.sendModalForm(
    player,
    "确认",
    "您确定要继续吗？",
    "是",
    "否",
    response -> {
        if (response != null) {
            if (response) {
                player.sendMessage("您确认了！");
            } else {
                player.sendMessage("您取消了！");
            }
        }
    }
);
```

**示例：传送确认**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

public void confirmTeleport(Player player, Location destination) {
    Easy4FormAPI.sendModalForm(
            player,
            "传送确认",
            "您要传送到指定位置吗？",
            "传送",
            "取消",
            response -> {
                if (response != null && response) {
                    player.teleport(destination);
                    player.sendMessage("已传送！");
                }
            }
    );
}
```

### 使用 ModalFormBuilder

**可用方法：**
- `title(String title)` - 设置表单标题
- `content(String content)` - 设置表单内容/描述
- `button1(String text)` - 设置第一个按钮的文本
- `button2(String text)` - 设置第二个按钮的文本
- `validResultHandler(Consumer<Boolean> handler)` - 设置有效响应的处理器
- `closedOrInvalidResultHandler(Runnable handler)` - 设置关闭或无效表单的处理器
- `send(Player player)` - 向玩家发送表单

**方法参数：**
- `title` (String) - 显示在表单顶部的标题文本
- `content` (String) - 显示在标题下方的描述文本
- `text` (String) - 按钮文本
- `handler` (Consumer<Boolean>) - 接收button1为`true`，button2为`false`的回调
- `handler` (Runnable) - 关闭/无效表单的回调
- `player` (Player) - 接收表单的Bukkit玩家

**用法：**
```java
new ModalFormBuilder()
    .title("确认")
    .content("您确定要继续吗？")
    .button1("是")
    .button2("否")
    .validResultHandler(response -> {
        if (response) {
            player.sendMessage("您确认了！");
        } else {
            player.sendMessage("您取消了！");
        }
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("表单被关闭");
    })
    .send(player);
```

**示例：删除家园确认**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.ModalFormBuilder;

public void confirmDeleteHome(Player player, String homeName) {
    new ModalFormBuilder()
            .title("删除家园")
            .content("您确定要删除家园 \"" + homeName + "\" 吗？")
            .button1("删除")
            .button2("取消")
            .validResultHandler(response -> {
                if (response) {
                    // 删除家园的代码
                    player.sendMessage("家园 \"" + homeName + "\" 已被删除。");
                }
            })
            .closedOrInvalidResultHandler(() -> {
                // 处理表单关闭
            })
            .send(player);
}
```

## 自定义表单

自定义表单允许您创建带有各种输入元素的表单，如文本框、开关、滑块、下拉菜单等。

### 使用 CustomFormBuilder

**可用方法：**
- `title(String title)` - 设置表单标题
- `label(String id, String text)` - 添加文本标签（仅显示）
- `input(String id, String label, String placeholder, String defaultValue)` - 添加文本输入框
- `toggle(String id, String label, boolean defaultValue)` - 添加开关（布尔值）
- `slider(String id, String label, float min, float max, float step, float defaultValue)` - 添加滑块
- `dropdown(String id, String label, List<String> options, int defaultIndex)` - 添加下拉菜单
- `stepSlider(String id, String label, List<String> steps, int defaultIndex)` - 添加步进滑块
- `validResultHandler(Consumer<Map<String, Object>> handler)` - 设置有效响应的处理器
- `invalidResultHandler(Runnable handler)` - 设置无效表单的处理器
- `send(Player player)` - 向玩家发送表单

**元素参数：**
- `id` (String) - 元素的唯一标识符（在响应映射中使用）
- `label` (String) - 元素的显示文本
- `text` (String) - 标签的文本内容
- `placeholder` (String) - 输入框的占位符文本
- `defaultValue` (String/boolean/float) - 元素的默认值
- `min/max` (float) - 滑块的范围限制
- `step` (float) - 滑块的步进增量
- `options/steps` (List<String>) - 下拉菜单/步进滑块的可用选项
- `defaultIndex` (int) - 下拉菜单/步进滑块的默认选中索引

**用法：**
```java
new CustomFormBuilder()
    .title("设置")
    .label("info", "请在下方配置您的设置：")
    .input("name", "姓名", "输入您的姓名", player.getName())
    .toggle("notifications", "启用通知", true)
    .slider("volume", "音量", 0, 100, 10, 50)
    .dropdown("difficulty", "难度", Arrays.asList("简单", "普通", "困难"), 1)
    .validResultHandler(response -> {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        int volume = ((Float) response.get("volume")).intValue();
        String difficulty = (String) response.get("difficulty");
        
        player.sendMessage("姓名：" + name);
        player.sendMessage("通知：" + notifications);
        player.sendMessage("音量：" + volume);
        player.sendMessage("难度：" + difficulty);
    })
    .invalidResultHandler(() -> {
        player.sendMessage("表单被关闭或无效");
    })
    .send(player);
```

**示例：玩家资料表单**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.CustomFormBuilder;

import java.util.Arrays;
import java.util.List;

public void showProfileForm(Player player) {
    List<String> genders = Arrays.asList("男性", "女性", "其他", "不愿透露");
    List<String> ageRanges = Arrays.asList("18岁以下", "18-24", "25-34", "35-44", "45+", "不愿透露");

    new CustomFormBuilder()
            .title("玩家资料")
            .label("header", "请填写您的资料信息：")
            .input("nickname", "昵称", "输入您的首选昵称", player.getName())
            .input("bio", "个人简介", "介绍一下您自己", "")
            .toggle("showProfile", "向其他玩家显示我的资料", true)
            .dropdown("gender", "性别", genders, 0)
            .dropdown("ageRange", "年龄段", ageRanges, 0)
            .validResultHandler(response -> {
                // 保存资料信息
                String nickname = (String) response.get("nickname");
                String bio = (String) response.get("bio");
                boolean showProfile = (boolean) response.get("showProfile");
                String gender = genders.get(((Float) response.get("gender")).intValue());
                String ageRange = ageRanges.get(((Float) response.get("ageRange")).intValue());

                // 保存资料信息的代码
                player.sendMessage("资料更新成功！");
            })
            .invalidResultHandler(() -> {
                // 处理表单关闭或无效输入
            })
            .send(player);
}
```

**示例：服务器反馈表单**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.CustomFormBuilder;

import java.util.Arrays;
import java.util.List;

public void showFeedbackForm(Player player) {
    List<String> ratings = Arrays.asList("差", "一般", "好", "很好", "优秀");
    List<String> aspects = Arrays.asList("游戏玩法", "社区", "管理员", "活动", "性能");

    new CustomFormBuilder()
            .title("服务器反馈")
            .label("intro", "我们重视您的反馈！请告诉我们您的想法。")
            .dropdown("overall", "整体体验", ratings, 2)
            .stepSlider("favorite", "最喜欢的方面", aspects, 0)
            .input("suggestions", "建议", "在此输入您的建议", "")
            .toggle("contact", "我们可以联系您获取更多反馈吗？", false)
            .validResultHandler(response -> {
                String overallRating = ratings.get(((Float) response.get("overall")).intValue());
                String favoriteAspect = aspects.get(((Float) response.get("favorite")).intValue());
                String suggestions = (String) response.get("suggestions");
                boolean canContact = (boolean) response.get("contact");

                // 保存反馈的代码
                player.sendMessage("感谢您的反馈！");

                // 通知管理员新反馈
                for (Player admin : getAdminPlayers()) {
                    admin.sendMessage("来自 " + player.getName() + " 的新反馈：");
                    admin.sendMessage("整体：" + overallRating);
                    admin.sendMessage("最喜欢：" + favoriteAspect);
                    admin.sendMessage("建议：" + suggestions);
                }
            })
            .invalidResultHandler(() -> {
                // 处理表单关闭
            })
            .send(player);
}

private List<Player> getAdminPlayers() {
    // 获取在线管理员玩家的代码
    return new ArrayList<>();
}
```

## 从 v1 迁移

### 主要变化

1. **响应处理**：v2 为不同的响应类型使用单独的处理器：
   - `validResultHandler()` - 用于有效的表单响应
   - `closedOrInvalidResultHandler()` / `invalidResultHandler()` - 用于关闭或无效的表单

2. **自定义表单响应访问**：v2 使用新的 Cumulus 迭代器模式：
   - v1：`response.responses().get(index)`
   - v2：`response.valueAt(index)` 配合 `response.reset()`

3. **Lambda 表达式**：v2 使用更简洁的 lambda 表达式以获得更好的性能

4. **包结构**：从 `cn.enderrealm.easy4form.api.v2` 导入而不是 `v1`

### 迁移示例

**v1 代码：**
```java
new SimpleFormBuilder()
    .title("测试")
    .button("按钮 1")
    .responseHandler(response -> {
        if (response != null) {
            player.sendMessage("点击了：" + response);
        } else {
            player.sendMessage("关闭了");
        }
    })
    .send(player);
```

**v2 代码：**
```java
new SimpleFormBuilder()
    .title("测试")
    .button("按钮 1")
    .validResultHandler(response -> {
        player.sendMessage("点击了：" + response);
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("关闭了");
    })
    .send(player);
```

## 附加说明

- 所有表单构建器都使用流式接口，允许方法链式调用以获得更清洁的代码。
- 响应处理器是异步调用的，因此在与 Bukkit API 交互时要小心。
- v2 相比 v1 提供了更好的错误处理和调试功能。
- 新的响应处理系统允许对不同表单状态进行更精确的控制。
- Lambda 表达式在 v2 中进行了优化以获得更好的性能。