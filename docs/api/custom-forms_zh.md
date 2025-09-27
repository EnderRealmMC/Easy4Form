# 自定义表单 - Easy4Form 主代理 API

**语言**: [English](custom-forms.md) | [中文](custom-forms_zh.md)

**返回**: [主 API 文档](README_ZH.md)

自定义表单是 Easy4Form 中最强大和灵活的表单类型。它们允许您从用户那里收集各种类型的输入，包括文本、数字、开关、下拉菜单和滑块。非常适合配置屏幕、用户注册、设置面板以及任何需要结构化数据输入的场景。

## 目录

- [概述](#概述)
- [基本用法](#基本用法)
- [表单元素](#表单元素)
- [高级功能](#高级功能)
- [构建器模式](#构建器模式)
- [最佳实践](#最佳实践)
- [示例](#示例)
- [故障排除](#故障排除)

## 概述

### 什么是自定义表单？

自定义表单提供：
- **多种输入类型**：文本、数字、开关、下拉菜单、滑块
- **结构化数据收集**：带有标签和描述的有组织表单
- **灵活布局**：可以按任何顺序添加元素
- **丰富响应**：返回与每个表单元素对应的值列表
- **验证支持**：内置和自定义验证选项

### 何时使用自定义表单

✅ **适用于：**
- 用户注册和个人资料
- 配置和设置面板
- 数据输入表单
- 调查和问卷
- 复杂输入收集
- 多步骤向导

❌ **不适用于：**
- 简单的是/否问题（使用[模态表单](modal-forms_zh.md)）
- 菜单导航（使用[简单表单](simple-forms_zh.md)）
- 单选选择（使用[简单表单](simple-forms_zh.md)）

## 基本用法

### 简单示例

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import cn.enderrealm.easy4form.api.CustomFormBuilder;

public void showPlayerRegistration(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("玩家注册")
        .addInput("username", "用户名：", "输入您的显示名称")
        .addInput("email", "邮箱：", "your@email.com")
        .addToggle("newsletter", "订阅新闻通讯", false)
        .addDropdown("region", "选择地区：", 
                    Arrays.asList("北美", "欧洲", "亚洲", "其他"), 0);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            String username = response.getInput("username");
            String email = response.getInput("email");
            boolean newsletter = response.getToggle("newsletter");
            String region = response.getDropdown("region");
            
            // 处理注册
            registerPlayer(player, username, email, newsletter, region);
            player.sendMessage("注册成功完成！");
        } else {
            player.sendMessage("注册已取消。");
        }
    });
}
```

### 响应处理

自定义表单响应提供对表单数据的类型化访问：

```java
response -> {
    if (response == null) {
        // 表单被关闭而没有提交
        player.sendMessage("表单已取消。");
        return;
    }
    
    // 通过元素 ID 访问表单数据
    String textValue = response.getInput("elementId");
    boolean toggleValue = response.getToggle("toggleId");
    String dropdownValue = response.getDropdown("dropdownId");
    double sliderValue = response.getSlider("sliderId");
    
    // 处理收集的数据
    processFormData(player, textValue, toggleValue, dropdownValue, sliderValue);
}
```

## 表单元素

### 1. 文本输入

从用户收集文本输入：

```java
// 基本文本输入
form.addInput("name", "玩家姓名：", "输入您的姓名");

// 带验证
form.addInput("username", "用户名：", "3-16个字符", input -> {
    if (input.length() < 3 || input.length() > 16) {
        return "用户名必须是3-16个字符长";
    }
    if (!input.matches("[a-zA-Z0-9_]+")) {
        return "用户名只能包含字母、数字和下划线";
    }
    return null; // 有效
});

// 多行文本输入
form.addTextArea("description", "描述：", "输入详细描述...");
```

### 2. 开关（布尔值）

提供开/关开关：

```java
// 基本开关
form.addToggle("pvp", "启用 PvP", false); // 默认：false

// 带描述
form.addToggle("notifications", "接收通知", true, 
              "获取重要服务器事件的通知");
```

### 3. 下拉菜单（选择）

提供多选选择：

```java
// 基本下拉菜单
List<String> options = Arrays.asList("简单", "普通", "困难", "专家");
form.addDropdown("difficulty", "难度等级：", options, 1); // 默认："普通"

// 动态下拉菜单
List<String> availableWorlds = getAvailableWorlds();
form.addDropdown("world", "选择世界：", availableWorlds, 0);

// 带自定义显示值
Map<String, String> regionMap = new HashMap<>();
regionMap.put("na", "北美");
regionMap.put("eu", "欧洲");
regionMap.put("asia", "亚太地区");
form.addDropdown("region", "地区：", regionMap, "na");
```

### 4. 滑块（数值范围）

允许在范围内进行数值输入：

```java
// 基本滑块
form.addSlider("volume", "音量等级：", 0.0, 100.0, 1.0, 50.0); // 最小值、最大值、步长、默认值

// 带自定义格式
form.addSlider("speed", "移动速度：", 0.1, 2.0, 0.1, 1.0, 
              value -> String.format("%.1fx", value));

// 整数滑块
form.addIntSlider("players", "最大玩家数：", 1, 20, 1, 10);
```

### 5. 数字输入

直接数值输入：

```java
// 基本数字输入
form.addNumber("amount", "数量：", "输入数量", 0.0);

// 带范围验证
form.addNumber("price", "价格：", "$0.00", 0.0, number -> {
    if (number < 0) {
        return "价格不能为负数";
    }
    if (number > 10000) {
        return "价格不能超过 $10,000";
    }
    return null; // 有效
});

// 整数输入
form.addInteger("quantity", "数量：", "输入数量", 1);
```

## 高级功能

### 条件元素

根据其他输入显示/隐藏元素：

```java
public void showConditionalForm(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("服务器设置")
        .addToggle("enablePvP", "启用 PvP", false)
        .addConditionalGroup("pvpSettings", "enablePvP", true, group -> {
            group.addSlider("pvpDamage", "PvP 伤害倍数：", 0.1, 3.0, 0.1, 1.0);
            group.addToggle("friendlyFire", "友军伤害", false);
            group.addDropdown("pvpZones", "PvP 区域：", 
                            Arrays.asList("任何地方", "指定区域", "仅竞技场"), 1);
        })
        .addToggle("enableEconomy", "启用经济", true)
        .addConditionalGroup("economySettings", "enableEconomy", true, group -> {
            group.addNumber("startingMoney", "起始金钱：", "$100.00", 100.0);
            group.addSlider("taxRate", "税率 (%)：", 0.0, 25.0, 0.5, 5.0);
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            processServerSettings(player, response);
        }
    });
}
```

### 表单验证

在提交前验证整个表单：

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("用户账户")
    .addInput("username", "用户名：", "输入用户名")
    .addInput("password", "密码：", "输入密码")
    .addInput("confirmPassword", "确认密码：", "确认密码")
    .addFormValidator(response -> {
        String password = response.getInput("password");
        String confirmPassword = response.getInput("confirmPassword");
        
        if (!password.equals(confirmPassword)) {
            return "密码不匹配";
        }
        
        if (password.length() < 8) {
            return "密码必须至少8个字符长";
        }
        
        return null; // 有效
    });
```

### 多页表单

创建向导式多步骤表单：

```java
public void showRegistrationWizard(Player player) {
    showRegistrationStep1(player, new HashMap<>());
}

private void showRegistrationStep1(Player player, Map<String, Object> data) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("注册 - 第 1 步，共 3 步")
        .addLabel("个人信息")
        .addInput("firstName", "名：", "输入名")
        .addInput("lastName", "姓：", "输入姓")
        .addInput("email", "邮箱：", "your@email.com");
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            data.put("firstName", response.getInput("firstName"));
            data.put("lastName", response.getInput("lastName"));
            data.put("email", response.getInput("email"));
            
            showRegistrationStep2(player, data);
        } else {
            player.sendMessage("注册已取消。");
        }
    });
}

private void showRegistrationStep2(Player player, Map<String, Object> data) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("注册 - 第 2 步，共 3 步")
        .addLabel("偏好设置")
        .addDropdown("region", "地区：", 
                    Arrays.asList("北美", "欧洲", "亚洲", "其他"), 0)
        .addToggle("newsletter", "订阅新闻通讯", false)
        .addSlider("experience", "游戏经验（年）：", 0, 20, 1, 5);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            data.put("region", response.getDropdown("region"));
            data.put("newsletter", response.getToggle("newsletter"));
            data.put("experience", response.getSlider("experience"));
            
            showRegistrationStep3(player, data);
        } else {
            // 返回第 1 步
            showRegistrationStep1(player, data);
        }
    });
}

private void showRegistrationStep3(Player player, Map<String, Object> data) {
    // 最终确认步骤
    String summary = String.format(
        "请确认您的注册信息：\n\n" +
        "姓名：%s %s\n" +
        "邮箱：%s\n" +
        "地区：%s\n" +
        "新闻通讯：%s\n" +
        "经验：%.0f 年",
        data.get("firstName"), data.get("lastName"),
        data.get("email"), data.get("region"),
        data.get("newsletter"), data.get("experience")
    );
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("注册 - 第 3 步，共 3 步")
        .addLabel(summary)
        .addToggle("confirm", "我确认此信息正确", false);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null && response.getToggle("confirm")) {
            // 完成注册
            completeRegistration(player, data);
            player.sendMessage("注册成功完成！");
        } else if (response != null) {
            player.sendMessage("请确认您的信息以完成注册。");
            showRegistrationStep3(player, data);
        } else {
            // 返回第 2 步
            showRegistrationStep2(player, data);
        }
    });
}
```

## 构建器模式

CustomFormBuilder 为表单创建提供流畅的 API：

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("表单标题")                                      // 设置表单标题
    .addLabel("节标题")                                     // 添加描述性文本
    .addInput("id", "标签：", "占位符")                      // 添加文本输入
    .addToggle("id", "标签", defaultValue)                 // 添加开关
    .addDropdown("id", "标签：", options, defaultIndex)     // 添加下拉菜单
    .addSlider("id", "标签：", min, max, step, defaultValue) // 添加滑块
    .addNumber("id", "标签：", "占位符", defaultValue)       // 添加数字输入
    .addSeparator()                                        // 添加视觉分隔符
    .setResponseHandler(response -> { ... })               // 设置响应处理器
    .setErrorHandler(error -> { ... });                   // 设置错误处理器

// 发送表单
Easy4FormAPI.sendCustomForm(player, form, responseHandler);
```

## 最佳实践

### 1. 使用标签和分隔符组织

```java
// ✅ 好 - 组织良好
CustomFormBuilder form = new CustomFormBuilder()
    .title("玩家设置")
    .addLabel("🎮 游戏偏好")
    .addDropdown("difficulty", "难度：", difficulties, 1)
    .addToggle("autoSave", "自动保存", true)
    .addSeparator()
    .addLabel("🔊 音频设置")
    .addSlider("volume", "主音量：", 0, 100, 5, 75)
    .addToggle("soundEffects", "音效", true)
    .addSeparator()
    .addLabel("📱 通知")
    .addToggle("chatNotifications", "聊天通知", true);

// ❌ 避免 - 没有组织
CustomFormBuilder form = new CustomFormBuilder()
    .title("设置")
    .addDropdown("difficulty", "难度：", difficulties, 1)
    .addSlider("volume", "音量：", 0, 100, 5, 75)
    .addToggle("autoSave", "自动保存", true)
    .addToggle("soundEffects", "音效", true);
```

### 2. 使用描述性标签和占位符

```java
// ✅ 好 - 清晰且描述性
form.addInput("serverName", "服务器名称：", "输入唯一的服务器名称（3-32个字符）");
form.addNumber("maxPlayers", "最大玩家数：", "1-100个玩家", 20.0);
form.addSlider("difficulty", "难度倍数：", 0.5, 3.0, 0.1, 1.0, 
              value -> String.format("%.1fx (%.0f%% 伤害)", value, value * 100));

// ❌ 避免 - 模糊标签
form.addInput("name", "名称：", "名称");
form.addNumber("number", "数字：", "数字", 0.0);
form.addSlider("slider", "值：", 0, 100, 1, 50);
```

### 3. 提供合理的默认值

```java
// ✅ 好 - 深思熟虑的默认值
form.addToggle("enablePvP", "启用 PvP", false);           // 安全默认值
form.addSlider("spawnRate", "怪物生成率：", 0.1, 3.0, 0.1, 1.0); // 正常速率
form.addDropdown("gamemode", "默认游戏模式：", 
                Arrays.asList("生存", "创造", "冒险"), 0); // 生存

// ❌ 避免 - 不良默认值
form.addToggle("deleteAllData", "删除所有数据", true);    // 危险默认值
form.addSlider("difficulty", "难度：", 0, 10, 1, 10);   // 最大难度
```

### 4. 适当验证输入

```java
// ✅ 好 - 全面验证
form.addInput("username", "用户名：", "3-16个字符，仅字母/数字", input -> {
    if (input.trim().isEmpty()) {
        return "用户名不能为空";
    }
    if (input.length() < 3 || input.length() > 16) {
        return "用户名必须是3-16个字符长";
    }
    if (!input.matches("[a-zA-Z0-9_]+")) {
        return "用户名只能包含字母、数字和下划线";
    }
    if (isUsernameTaken(input)) {
        return "用户名已被占用";
    }
    return null;
});

// ❌ 避免 - 没有验证
form.addInput("username", "用户名：", "输入用户名"); // 没有验证
```

### 5. 优雅地处理错误

```java
Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response == null) {
        player.sendMessage("表单已取消。");
        return;
    }
    
    try {
        // 处理表单数据
        processFormData(player, response);
        player.sendMessage("设置保存成功！");
    } catch (ValidationException e) {
        player.sendMessage("输入无效：" + e.getMessage());
        // 可选择重新显示带错误消息的表单
        showFormWithError(player, e.getMessage());
    } catch (Exception e) {
        player.sendMessage("保存设置时发生错误。请重试。");
        getLogger().severe("表单处理错误：" + e.getMessage());
    }
});
```

## 示例

### 示例 1：服务器配置面板

```java
public void showServerConfig(Player admin) {
    if (!admin.hasPermission("server.admin")) {
        admin.sendMessage("您没有访问服务器配置的权限。");
        return;
    }
    
    // 获取当前服务器设置
    ServerConfig config = getServerConfig();
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("🔧 服务器配置")
        
        // 基本设置
        .addLabel("📋 基本设置")
        .addInput("serverName", "服务器名称：", "输入服务器名称", config.getServerName())
        .addInput("motd", "每日消息：", "欢迎消息", config.getMotd())
        .addIntSlider("maxPlayers", "最大玩家数：", 1, 100, 1, config.getMaxPlayers())
        .addSeparator()
        
        // 游戏设置
        .addLabel("🎮 游戏设置")
        .addDropdown("difficulty", "难度：", 
                    Arrays.asList("和平", "简单", "普通", "困难"), 
                    config.getDifficultyIndex())
        .addToggle("pvp", "启用 PvP", config.isPvpEnabled())
        .addToggle("mobSpawning", "怪物生成", config.isMobSpawningEnabled())
        .addSlider("mobSpawnRate", "怪物生成率：", 0.1, 3.0, 0.1, config.getMobSpawnRate(),
                  value -> String.format("%.1fx", value))
        .addSeparator()
        
        // 世界设置
        .addLabel("世界设置")
        .addToggle("generateStructures", "生成结构", config.isGenerateStructures())
        .addToggle("allowNether", "允许下界", config.isAllowNether())
        .addToggle("allowEnd", "允许末地", config.isAllowEnd())
        .addIntSlider("viewDistance", "视距：", 3, 32, 1, config.getViewDistance())
        .addSeparator()
        
        // 经济设置（如果启用了经济插件）
        .addConditionalGroup("economyGroup", () -> isEconomyEnabled(), group -> {
            group.addLabel("经济设置");
            group.addNumber("startingMoney", "起始金钱：", "$0.00", config.getStartingMoney());
            group.addSlider("shopTaxRate", "商店税率 (%)：", 0.0, 25.0, 0.5, config.getShopTaxRate());
            group.addToggle("allowPlayerShops", "允许玩家商店", config.isAllowPlayerShops());
        })
        
        // 验证
        .addFormValidator(response -> {
            String serverName = response.getInput("serverName");
            if (serverName.trim().isEmpty()) {
                return "服务器名称不能为空";
            }
            if (serverName.length() > 50) {
                return "服务器名称不能超过50个字符";
            }
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(admin, form, response -> {
        if (response != null) {
            try {
                // 应用配置更改
                applyServerConfig(response);
                admin.sendMessage("服务器配置更新成功！");
                
                // 记录更改
                getLogger().info(String.format("服务器配置由 %s 更新", admin.getName()));
                
                // 通知其他管理员
                notifyAdmins(String.format("%s 更新了服务器配置", admin.getName()));
                
            } catch (Exception e) {
                admin.sendMessage("更新服务器配置时出错：" + e.getMessage());
                getLogger().severe("服务器配置更新失败：" + e.getMessage());
            }
        } else {
            admin.sendMessage("服务器配置更新已取消。");
        }
    });
}

private void applyServerConfig(CustomFormResponse response) {
    ServerConfig config = getServerConfig();
    
    config.setServerName(response.getInput("serverName"));
    config.setMotd(response.getInput("motd"));
    config.setMaxPlayers((int) response.getSlider("maxPlayers"));
    config.setDifficulty(response.getDropdownIndex("difficulty"));
    config.setPvpEnabled(response.getToggle("pvp"));
    config.setMobSpawningEnabled(response.getToggle("mobSpawning"));
    config.setMobSpawnRate(response.getSlider("mobSpawnRate"));
    config.setGenerateStructures(response.getToggle("generateStructures"));
    config.setAllowNether(response.getToggle("allowNether"));
    config.setAllowEnd(response.getToggle("allowEnd"));
    config.setViewDistance((int) response.getSlider("viewDistance"));
    
    if (isEconomyEnabled()) {
        config.setStartingMoney(response.getNumber("startingMoney"));
        config.setShopTaxRate(response.getSlider("shopTaxRate"));
        config.setAllowPlayerShops(response.getToggle("allowPlayerShops"));
    }
    
    config.save();
    config.apply();
}
```

### 示例 2：玩家个人资料创建

```java
public void showProfileCreation(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("创建您的个人资料")
        
        // 个人信息
        .addLabel("个人信息")
        .addInput("displayName", "显示名称：", "其他人将如何看到您", player.getName())
        .addTextArea("bio", "个人简介：", "告诉其他人关于您的信息（可选）")
        .addDropdown("timezone", "时区：", getTimezones(), getDefaultTimezoneIndex())
        .addSeparator()
        
        // 游戏偏好
        .addLabel("游戏偏好")
        .addDropdown("favoriteGamemode", "最喜欢的游戏模式：", 
                    Arrays.asList("生存", "创造", "冒险", "观察者"), 0)
        .addSlider("skillLevel", "技能等级：", 1, 10, 1, 5,
                  value -> {
                      String[] levels = {"初学者", "新手", "中级", 
                                       "高级", "专家", "大师"};
                      int index = Math.min((int)((value - 1) / 2), levels.length - 1);
                      return String.format("%.0f - %s", value, levels[index]);
                  })
        .addMultiSelect("interests", "兴趣：", 
                       Arrays.asList("建筑", "红石", "PvP", "探索", 
                                   "农业", "交易", "小游戏"))
        .addSeparator()
        
        // 隐私设置
        .addLabel("隐私设置")
        .addToggle("showOnlineStatus", "向其他人显示在线状态", true)
        .addToggle("allowFriendRequests", "允许好友请求", true)
        .addToggle("showStats", "公开显示统计信息", false)
        .addDropdown("profileVisibility", "个人资料可见性：", 
                    Arrays.asList("公开", "仅好友", "私人"), 0)
        .addSeparator()
        
        // 通知偏好
        .addLabel("通知")
        .addToggle("emailNotifications", "邮件通知", false)
        .addToggle("friendOnlineNotifications", "好友上线通知", true)
        .addToggle("eventNotifications", "服务器事件通知", true)
        
        // 验证
        .addFormValidator(response -> {
            String displayName = response.getInput("displayName").trim();
            if (displayName.isEmpty()) {
                return "显示名称不能为空";
            }
            if (displayName.length() < 3 || displayName.length() > 20) {
                return "显示名称必须是3-20个字符长";
            }
            if (!displayName.matches("[a-zA-Z0-9_\\s]+")) {
                return "显示名称只能包含字母、数字、空格和下划线";
            }
            if (isDisplayNameTaken(displayName, player)) {
                return "显示名称已被占用";
            }
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            try {
                // 创建玩家个人资料
                PlayerProfile profile = createPlayerProfile(player, response);
                savePlayerProfile(profile);
                
                player.sendMessage("个人资料创建成功！");
                player.sendMessage("欢迎来到服务器，" + response.getInput("displayName") + "！");
                
                // 显示欢迎导览或下一步
                showWelcomeTour(player);
                
            } catch (Exception e) {
                player.sendMessage("创建个人资料时出错：" + e.getMessage());
                getLogger().severe("为 " + player.getName() + " 创建个人资料失败：" + e.getMessage());
            }
        } else {
            player.sendMessage("个人资料创建已取消。");
            // 也许显示简化的快速设置
            showQuickSetup(player);
        }
    });
}

private PlayerProfile createPlayerProfile(Player player, CustomFormResponse response) {
    PlayerProfile profile = new PlayerProfile(player.getUniqueId());
    
    profile.setDisplayName(response.getInput("displayName"));
    profile.setBio(response.getInput("bio"));
    profile.setTimezone(response.getDropdown("timezone"));
    profile.setFavoriteGamemode(response.getDropdown("favoriteGamemode"));
    profile.setSkillLevel((int) response.getSlider("skillLevel"));
    profile.setInterests(response.getMultiSelect("interests"));
    profile.setShowOnlineStatus(response.getToggle("showOnlineStatus"));
    profile.setAllowFriendRequests(response.getToggle("allowFriendRequests"));
    profile.setShowStats(response.getToggle("showStats"));
    profile.setProfileVisibility(response.getDropdown("profileVisibility"));
    profile.setEmailNotifications(response.getToggle("emailNotifications"));
    profile.setFriendOnlineNotifications(response.getToggle("friendOnlineNotifications"));
    profile.setEventNotifications(response.getToggle("eventNotifications"));
    
    return profile;
}
```

### 示例 3：商店创建表单

```java
public void showShopCreation(Player player) {
    if (!player.hasPermission("shops.create")) {
        player.sendMessage("您没有创建商店的权限。");
        return;
    }
    
    // 检查玩家是否达到商店限制
    int currentShops = getPlayerShopCount(player);
    int maxShops = getMaxShopsForPlayer(player);
    
    if (currentShops >= maxShops) {
        player.sendMessage(String.format("您已达到商店限制 (%d/%d)", currentShops, maxShops));
        return;
    }
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("创建新商店")
        
        // 基本商店信息
        .addLabel("商店信息")
        .addInput("shopName", "商店名称：", "输入唯一的商店名称", "")
        .addTextArea("description", "描述：", "描述您的商店销售什么（可选）")
        .addDropdown("category", "商店类别：", 
                    Arrays.asList("综合", "食物", "工具", "武器", "盔甲", 
                                "建筑", "红石", "装饰", "其他"), 0)
        .addSeparator()
        
        // 位置设置
        .addLabel("位置设置")
        .addToggle("useCurrentLocation", "使用当前位置", true)
        .addConditionalGroup("customLocation", "useCurrentLocation", false, group -> {
            group.addNumber("locationX", "X 坐标：", "输入 X 坐标", 0.0);
            group.addNumber("locationY", "Y 坐标：", "输入 Y 坐标", 64.0);
            group.addNumber("locationZ", "Z 坐标：", "输入 Z 坐标", 0.0);
            group.addDropdown("world", "世界：", getWorldNames(), 0);
        })
        .addSeparator()
        
        // 商店设置
        .addLabel("商店设置")
        .addToggle("isPublic", "公开商店", true, "允许其他玩家找到并访问您的商店")
        .addToggle("allowVisitors", "允许访客", true, "让玩家无需购买即可浏览")
        .addSlider("shopSize", "商店大小：", 1, 5, 1, 3,
                  value -> {
                      String[] sizes = {"微型 (9 物品)", "小型 (18 物品)", "中型 (27 物品)", 
                                      "大型 (36 物品)", "巨型 (45 物品)"};
                      return sizes[(int)value - 1];
                  })
        .addSeparator()
        
        // 经济设置
        .addLabel("经济设置")
        .addNumber("setupCost", "设置费用：", "创建此商店的费用", getShopSetupCost(player))
        .addSlider("taxRate", "税率 (%)：", 0.0, 10.0, 0.5, 2.0,
                  value -> String.format("%.1f%% (您保留 %.1f%%)", value, 100 - value))
        .addToggle("autoRestock", "从库存自动补货", false)
        .addConditionalGroup("restockSettings", "autoRestock", true, group -> {
            group.addIntSlider("restockInterval", "补货间隔（分钟）：", 5, 60, 5, 15);
            group.addToggle("notifyLowStock", "库存不足时通知", true);
        })
        
        // 验证
        .addFormValidator(response -> {
            String shopName = response.getInput("shopName").trim();
            if (shopName.isEmpty()) {
                return "商店名称不能为空";
            }
            if (shopName.length() < 3 || shopName.length() > 30) {
                return "商店名称必须是3-30个字符长";
            }
            if (isShopNameTaken(shopName)) {
                return "商店名称已被占用";
            }
            
            double setupCost = response.getNumber("setupCost");
            if (getPlayerBalance(player) < setupCost) {
                return String.format("资金不足。您需要 $%.2f 但只有 $%.2f", 
                                   setupCost, getPlayerBalance(player));
            }
            
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            try {
                // 创建商店
                Shop shop = createShop(player, response);
                
                // 收取设置费用
                double setupCost = response.getNumber("setupCost");
                removePlayerBalance(player, setupCost);
                
                player.sendMessage(String.format("商店 '%s' 创建成功！", shop.getName()));
                player.sendMessage(String.format("设置费用：$%.2f", setupCost));
                player.sendMessage("使用 /shop manage 向您的商店添加物品。");
                
                // 如果请求，传送到商店位置
                if (response.getToggle("useCurrentLocation")) {
                    player.sendMessage("您的商店已在您当前位置创建。");
                } else {
                    player.sendMessage("输入 /shop visit " + shop.getName() + " 访问您的新商店。");
                }
                
                // 记录商店创建
                getLogger().info(String.format("玩家 %s 在 %s 创建了商店 '%s'", 
                                              player.getName(), shop.getLocation(), shop.getName()));
                
            } catch (Exception e) {
                player.sendMessage("创建商店时出错：" + e.getMessage());
                getLogger().severe("商店创建失败：" + e.getMessage());
            }
        } else {
            player.sendMessage("商店创建已取消。");
        }
    });
}

private Shop createShop(Player player, CustomFormResponse response) {
    Shop shop = new Shop();
    
    shop.setOwner(player.getUniqueId());
    shop.setName(response.getInput("shopName"));
    shop.setDescription(response.getInput("description"));
    shop.setCategory(response.getDropdown("category"));
    
    // 设置位置
    if (response.getToggle("useCurrentLocation")) {
        shop.setLocation(player.getLocation());
    } else {
        double x = response.getNumber("locationX");
        double y = response.getNumber("locationY");
        double z = response.getNumber("locationZ");
        String worldName = response.getDropdown("world");
        World world = Bukkit.getWorld(worldName);
        shop.setLocation(new Location(world, x, y, z));
    }
    
    shop.setPublic(response.getToggle("isPublic"));
    shop.setAllowVisitors(response.getToggle("allowVisitors"));
    shop.setSize((int) response.getSlider("shopSize"));
    shop.setTaxRate(response.getSlider("taxRate"));
    shop.setAutoRestock(response.getToggle("autoRestock"));
    
    if (response.getToggle("autoRestock")) {
        shop.setRestockInterval((int) response.getSlider("restockInterval"));
        shop.setNotifyLowStock(response.getToggle("notifyLowStock"));
    }
    
    shop.save();
    return shop;
}
```

## 故障排除

### 常见问题

**表单不显示：**
- 确保玩家是基岩版玩家
- 检查 Floodgate 是否正确安装
- 验证 Easy4Form 是否已启用
- 检查表单大小限制（元素过多）

**响应值为 null 或不正确：**
- 验证表单创建和响应处理之间的元素 ID 匹配
- 检查表单是否已提交或关闭
- 使用调试来记录响应值
- 确保数值的正确类型转换

**验证不工作：**
- 检查验证逻辑的边界情况
- 确保验证函数对有效输入返回 null
- 测试各种输入组合
- 记录验证结果以进行调试

**大型表单的性能问题：**
- 考虑将大型表单分解为多个步骤
- 使用条件组隐藏不相关的部分
- 限制下拉菜单选项的数量
- 优化验证逻辑

### 调试示例

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("调试表单")
    .addInput("testInput", "测试输入：", "输入文本")
    .addToggle("testToggle", "测试开关", false)
    .addDropdown("testDropdown", "测试下拉菜单：", 
                Arrays.asList("选项 1", "选项 2", "选项 3"), 0)
    .addSlider("testSlider", "测试滑块：", 0, 100, 10, 50);

Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response == null) {
        getLogger().info("表单被 " + player.getName() + " 关闭");
        return;
    }
    
    // 记录所有响应值以进行调试
    getLogger().info("来自 " + player.getName() + " 的表单响应：");
    getLogger().info("  输入：'" + response.getInput("testInput") + "'");
    getLogger().info("  开关：" + response.getToggle("testToggle"));
    getLogger().info("  下拉菜单：'" + response.getDropdown("testDropdown") + "' (索引：" + 
                    response.getDropdownIndex("testDropdown") + ")");
    getLogger().info("  滑块：" + response.getSlider("testSlider"));
    
    player.sendMessage("表单提交成功！请查看控制台了解详情。");
});
```

## 相关文档

- **[简单表单](simple-forms_zh.md)** - 用于菜单导航和单选
- **[模态表单](modal-forms_zh.md)** - 用于是/否确认
- **[主 API 文档](README_ZH.md)** - 概述和入门
- **[玩家工具](README_ZH.md#玩家工具)** - 辅助方法

---

**下一步：**
- 尝试[简单表单指南](simple-forms_zh.md)来创建菜单
- 探索[模态表单](modal-forms_zh.md)进行确认
- 返回[主 API 文档](README_ZH.md)了解其他功能