# Easy4Form API 文档

Easy4Form 是一个基于 Floodgate 的简化 Form 接口，使得在 Java 服务器上为基岩版玩家创建和发送表单变得更加简单。

## 目录

- [简介](#简介)
- [玩家工具](#玩家工具)
- [简单表单](#简单表单)
- [模态表单](#模态表单)
- [自定义表单](#自定义表单)

## 简介

Easy4Form 提供了一个简化的接口，用于通过 Floodgate API 为基岩版玩家创建和发送表单。它支持基岩版中可用的所有三种表单类型：

- **简单表单**：玩家可以点击的按钮列表
- **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
- **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

## 玩家工具

### 检查玩家是否为基岩版玩家

```java
boolean isBedrockPlayer = Easy4FormAPI.isBedrockPlayer(player);
```

**示例：**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;

public void onPlayerJoin(Player player) {
    if (Easy4FormAPI.isBedrockPlayer(player)) {
        player.sendMessage("欢迎，基岩版玩家！");
    } else {
        player.sendMessage("欢迎，Java版玩家！");
    }
}
```

### 获取FloodgatePlayer实例

```java
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
```

**示例：**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public void getPlayerInfo(Player player) {
    FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
    if (floodgatePlayer != null) {
        String deviceOs = floodgatePlayer.getDeviceOs().toString();
        player.sendMessage("您正在使用的设备系统是：" + deviceOs);
    }
}
```

## 简单表单

简单表单由标题、内容和按钮列表组成。

### 使用Easy4FormAPI

```java
List<String> buttons = Arrays.asList("按钮1", "按钮2", "按钮3");
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    if (response != null) {
        player.sendMessage("你点击了按钮：" + buttons.get(response));
    } else {
        player.sendMessage("你关闭了表单");
    }
});
```

**示例：服务器选择器**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import java.util.Arrays;
import java.util.List;

public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList("生存服", "创造服", "小游戏", "空岛");
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "服务器选择器",
        "选择要连接的服务器：",
        servers,
        response -> {
            if (response != null) {
                String server = servers.get(response);
                player.sendMessage("正在连接到" + server + "...");
                // 连接玩家到选定服务器的代码
            }
        }
    );
}
```

### 使用SimpleFormBuilder

```java
new SimpleFormBuilder()
    .title("标题")
    .content("内容")
    .button("按钮1")                                        // 无图标按钮
    .button("按钮2", "textures/items/diamond")             // 资源包图标按钮
    .buttonWithUrl("按钮3", "https://example.com/image.png") // URL图标按钮
    .responseHandler(response -> {
        if (response != null) {
            player.sendMessage("你点击了按钮：" + response);
        }
    })
    .send(player);
```

**按钮方法说明：**
- `button(String text)` - 添加无图标的普通按钮
- `button(String text, String imagePath)` - 添加带资源包图标的按钮，图标路径使用Minecraft资源包路径格式
- `buttonWithUrl(String text, String imageUrl)` - 添加带URL图标的按钮，图标使用网络图片URL

**示例：商店分类**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.SimpleFormBuilder;

public void showShopCategories(Player player) {
    new SimpleFormBuilder()
        .title("商店")
        .content("选择一个分类：")
        .button("武器", "textures/items/diamond_sword")      // 带资源包图标的按钮
        .button("盔甲", "textures/items/diamond_chestplate") // 带资源包图标的按钮
        .button("工具", "textures/items/diamond_pickaxe")   // 带资源包图标的按钮
        .button("食物", "textures/items/apple")            // 带资源包图标的按钮
        .button("杂项")                                    // 无图标按钮
        .responseHandler(response -> {
            if (response == null) {
                return; // 表单被关闭
            }
            
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
        .send(player);
}

/**
 * 使用Easy4FormAPI发送带图标的简单表单示例
 */
public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList("生存服", "创造服", "小游戏", "空岛");
    List<String> images = Arrays.asList(
        "textures/blocks/grass_side",
        "textures/blocks/command_block",
        "textures/items/diamond",
        "textures/blocks/end_stone"
    );
    
    Easy4FormAPI.sendSimpleFormWithImages(
        player,
        "服务器选择器",
        "选择要连接的服务器：",
        servers,
        images,
        response -> {
            if (response != null) {
                String server = servers.get(response);
                player.sendMessage("正在连接到" + server + "...");
                // 连接玩家到选定服务器的代码
            }
        }
    );
}
```

## 模态表单

模态表单由标题、内容和两个按钮组成（通常用于是/否或确认/取消场景）。

### 使用Easy4FormAPI

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
                player.sendMessage("您已确认！");
            } else {
                player.sendMessage("您已取消！");
            }
        }
    }
);
```

**示例：传送确认**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;

public void confirmTeleport(Player player, Location destination) {
    Easy4FormAPI.sendModalForm(
        player,
        "传送确认",
        "您想要传送到指定位置吗？",
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

### 使用ModalFormBuilder

```java
new ModalFormBuilder()
    .title("确认")
    .content("您确定要继续吗？")
    .button1("是")
    .button2("否")
    .responseHandler(response -> {
        if (response != null) {
            if (response) {
                player.sendMessage("您已确认！");
            } else {
                player.sendMessage("您已取消！");
            }
        }
    })
    .send(player);
```

**示例：删除家确认**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.ModalFormBuilder;

public void confirmDeleteHome(Player player, String homeName) {
    new ModalFormBuilder()
        .title("删除家")
        .content("您确定要删除您的家 \"" + homeName + "\" 吗？")
        .button1("删除")
        .button2("取消")
        .responseHandler(response -> {
            if (response != null && response) {
                // 删除家的代码
                player.sendMessage("家 \"" + homeName + "\" 已被删除。");
            }
        })
        .send(player);
}
```

## 自定义表单

自定义表单允许您创建带有各种输入元素的表单，如文本框、开关、滑块、下拉菜单等。

### 使用Easy4FormAPI和CustomFormBuilder

```java
CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "设置", response -> {
    if (response != null) {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        int volume = ((Float) response.get("volume")).intValue();
        String difficulty = (String) response.get("difficulty");
        
        player.sendMessage("名称：" + name);
        player.sendMessage("通知：" + notifications);
        player.sendMessage("音量：" + volume);
        player.sendMessage("难度：" + difficulty);
    }
});

form.label("info", "请在下方配置您的设置：")
    .input("name", "名称", "输入您的名称", player.getName())
    .toggle("notifications", "启用通知", true)
    .slider("volume", "音量", 0, 100, 10, 50)
    .dropdown("difficulty", "难度", Arrays.asList("简单", "普通", "困难"), 1)
    .send(player);
```

**示例：玩家资料表单**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.CustomFormBuilder;
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public void showProfileForm(Player player) {
    List<String> genders = Arrays.asList("男", "女", "其他", "不愿透露");
    List<String> ageRanges = Arrays.asList("18岁以下", "18-24岁", "25-34岁", "35-44岁", "45岁以上", "不愿透露");
    
    CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "玩家资料", response -> {
        if (response == null) {
            return; // 表单被关闭
        }
        
        // 保存资料信息
        String nickname = (String) response.get("nickname");
        String bio = (String) response.get("bio");
        boolean showProfile = (boolean) response.get("showProfile");
        String gender = genders.get(((Float) response.get("gender")).intValue());
        String ageRange = ageRanges.get(((Float) response.get("ageRange")).intValue());
        
        // 保存资料信息的代码
        player.sendMessage("资料更新成功！");
    });
    
    form.label("header", "请填写您的资料信息：")
        .input("nickname", "昵称", "输入您的首选昵称", player.getName())
        .input("bio", "个人简介", "介绍一下您自己", "")
        .toggle("showProfile", "向其他玩家显示我的资料", true)
        .dropdown("gender", "性别", genders, 0)
        .dropdown("ageRange", "年龄段", ageRanges, 0)
        .send(player);
}
```

### 直接使用CustomFormBuilder

```java
new CustomFormBuilder()
    .title("设置")
    .label("info", "请在下方配置您的设置：")
    .input("name", "名称", "输入您的名称", player.getName())
    .toggle("notifications", "启用通知", true)
    .slider("volume", "音量", 0, 100, 10, 50)
    .dropdown("difficulty", "难度", Arrays.asList("简单", "普通", "困难"), 1)
    .responseHandler(response -> {
        if (response != null) {
            String name = (String) response.get("name");
            boolean notifications = (boolean) response.get("notifications");
            int volume = ((Float) response.get("volume")).intValue();
            String difficulty = (String) response.get("difficulty");
            
            player.sendMessage("设置已保存！");
        }
    })
    .send(player);
```

**示例：服务器反馈表单**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.CustomFormBuilder;
import java.util.Arrays;
import java.util.List;

public void showFeedbackForm(Player player) {
    List<String> ratings = Arrays.asList("差", "一般", "良好", "很好", "优秀");
    List<String> aspects = Arrays.asList("游戏玩法", "社区", "管理员", "活动", "性能");
    
    new CustomFormBuilder()
        .title("服务器反馈")
        .label("intro", "我们重视您的反馈！请让我们知道我们的表现如何。")
        .dropdown("overall", "整体体验", ratings, 2)
        .stepSlider("favorite", "最喜欢的方面", aspects, 0)
        .input("suggestions", "建议", "在此输入您的建议", "")
        .toggle("contact", "我们可以联系您获取更多反馈吗？", false)
        .responseHandler(response -> {
            if (response == null) {
                return; // 表单被关闭
            }
            
            String overallRating = ratings.get(((Float) response.get("overall")).intValue());
            String favoriteAspect = aspects.get(((Float) response.get("favorite")).intValue());
            String suggestions = (String) response.get("suggestions");
            boolean canContact = (boolean) response.get("contact");
            
            // 保存反馈的代码
            player.sendMessage("感谢您的反馈！");
            
            // 通知管理员有新反馈
            for (Player admin : getAdminPlayers()) {
                admin.sendMessage("来自 " + player.getName() + " 的新反馈：");
                admin.sendMessage("整体评价：" + overallRating);
                admin.sendMessage("最喜欢的方面：" + favoriteAspect);
                admin.sendMessage("建议：" + suggestions);
            }
        })
        .send(player);
}

private List<Player> getAdminPlayers() {
    // 获取在线管理员玩家的代码
    return new ArrayList<>();
}
```

## 附加说明

- 所有表单构建器都使用流畅的接口，允许方法链接以获得更清晰的代码。
- 响应处理器是异步调用的，所以在与Bukkit API交互时要小心。
- 如果玩家关闭表单而不选择选项，响应将为`null`。
- 对于自定义表单，响应是组件ID到其值的映射。
- API会在发送表单前自动检查玩家是否为基岩版玩家。

---

有关更多信息，请参阅[Floodgate文档](https://wiki.geysermc.org/floodgate/)和[Cumulus API文档](https://github.com/GeyserMC/Cumulus)。