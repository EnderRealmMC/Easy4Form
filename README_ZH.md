# Easy4Form

**语言**: [English](README.md) | [中文](README_ZH.md)

基于 Provider 的简化 Form 接口，使得在 Java 服务器上创建和发送表单变得更加简单。自 3.0.0 起，库采用公开门面加内部表单 Provider 的架构；当前 Provider 通过 Floodgate 与 Cumulus 发送基岩版表单，未来可在不改动门面的前提下接入 Java 版 Dialog Provider。

## 简介

Easy4Form是一个Bukkit/Spigot插件，提供了一个简化的接口，用于创建和发送表单。内置的 Cumulus Provider 通过 Floodgate API 为基岩版玩家发送表单。它支持基岩版中可用的所有三种表单类型：

- **简单表单**：玩家可以点击的按钮列表
- **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
- **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

## 要求

- 运行Bukkit/Spigot/Paper 1.18+的Minecraft服务器
- [Floodgate](https://github.com/GeyserMC/Floodgate)插件（基岩版表单 Provider 需要；缺失时自动禁用基岩版表单）

## 安装

1. 从[发布页面](https://github.com/EnderRealmMC/Easy4Form/releases)下载最新版本的Easy4Form
2. 将JAR文件放入服务器的`plugins`文件夹中
3. 重启服务器

## 使用方法（针对开发者）

要在您的项目中使用Easy4Form作为依赖项，您可以使用Maven或Gradle导入：

### Maven

将以下仓库添加到您的`pom.xml`中：

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/EnderRealmMC/Easy4Form</url>
    </repository>
</repositories>
```

然后添加依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>cn.enderrealm</groupId>
        <artifactId>easy4form</artifactId>
        <version>3.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

将以下仓库添加到您的`build.gradle`中：

```gradle
repositories {
    maven {
        url 'https://maven.pkg.github.com/EnderRealmMC/Easy4Form'
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

然后添加依赖项：

```gradle
dependencies {
    compileOnly 'cn.enderrealm:easy4form:3.0.0'
}
```

## 3.0.0 破坏性变更

- 公开门面仍为 `cn.enderrealm.easy4form.api.*`，现有构建器与 `Easy4FormAPI` 调用签名不变。
- 移除 `Easy4FormAPI.getFloodgatePlayer(...)`。
- `cn.enderrealm.easy4form.api.PlayerUtils` 迁移至 `cn.enderrealm.easy4form.utils.PlayerUtils`。
- 删除 `api.v1` 与 `api.v2`，表单改由内部 Provider 注册表分发。
- 表单关闭响应：简单表单与模态表单返回 `null`，自定义表单返回空 Map。

## 配置

Easy4Form包含一个配置文件（`config.yml`），具有以下选项：

```yaml
# 每个平台只能选择一个 Provider，none 表示禁用该平台表单
providers:
  bedrock-form: cumulus # cumulus | none
  java-dialog: none     # 预留，暂无可用实现

# 基岩版玩家检测模式
# 选项: floodgate（使用Floodgate API）, uuid（使用UUID前缀）
detection-mode: floodgate

# 基岩版玩家的UUID前缀（仅在detection-mode为'uuid'时使用）
uuid-prefix: "00000000-0000"

# 调试模式
debug: false
```

### Provider 选择

每个平台从 `config.yml` 中选择唯一的 Provider：

- `bedrock-form: cumulus` 启用 Cumulus Provider。它依赖 Floodgate；Floodgate 缺失时会自动跳过并禁用基岩版表单。
- `bedrock-form: none` 禁用基岩版表单，但不影响插件启用。
- `java-dialog: none` 为未来的 Java 版 Dialog Provider 预留。

### 检测模式

- **floodgate**：使用Floodgate API检测基岩版玩家（推荐）
- **uuid**：使用UUID前缀检测基岩版玩家（在Floodgate检测失败时有用）

您可以在`uuid`模式下自定义用于检测的UUID前缀。

## 特性

- **简化API**：易于使用的API，用于创建和发送表单
- **可配置 Provider**：按平台选择 Provider（`cumulus` 或 `none`）；Floodgate 不可用时基岩版表单自动降级
- **流畅接口**：所有表单构建器都使用流畅的接口，允许方法链接以获得更清晰的代码
- **类型安全**：响应处理器提供对表单响应的类型安全访问
- **全面的文档**：详细的文档，包含所有表单类型的示例
- **双语支持**：文档提供英文和中文两种语言版本

## 快速示例

### 简单表单

```java
List<String> buttons = Arrays.asList("按钮1", "按钮2", "按钮3");
Easy4FormAPI.sendSimpleForm(player, "标题", "内容", buttons, response -> {
    if (response != null) {
        player.sendMessage("你点击了按钮：" + buttons.get(response));
    }
});
```

### 模态表单

```java
Easy4FormAPI.sendModalForm(
    player,
    "确认",
    "您确定要继续吗？",
    "是",
    "否",
    response -> {
        if (response != null && response) {
            player.sendMessage("您已确认！");
        }
    }
);
```

### 自定义表单

```java
CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "设置", response -> {
    if (response != null) {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        player.sendMessage("设置已保存！");
    }
});

form.input("name", "名称", "输入您的名称", player.getName())
    .toggle("notifications", "启用通知", true)
    .send(player);
```



## 许可证

本项目采用MIT许可证 - 详情请参阅[LICENSE](LICENSE)文件。

## 贡献

请随时提交Pull Request。

## 致谢

- [Floodgate](https://github.com/GeyserMC/Floodgate) - 提供与基岩版玩家交互的API
- [Cumulus](https://github.com/GeyserMC/Cumulus) - Floodgate使用的表单库

## 支持

如果您遇到任何问题或有疑问，请在 GitHub 上[提交问题](https://github.com/EnderRealmMC/Easy4Form/issues)。

