# Easy4Form

基于Floodgate的简化Form接口，使得在Java服务器上为基岩版玩家创建和发送表单变得更加简单。

## 简介

Easy4Form是一个Bukkit/Spigot插件，提供了一个简化的接口，用于通过Floodgate API为基岩版玩家创建和发送表单。它支持基岩版中可用的所有三种表单类型：

- **简单表单**：玩家可以点击的按钮列表
- **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
- **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

## 要求

- 运行Bukkit/Spigot/Paper 1.18+的Minecraft服务器
- 安装了[Floodgate](https://github.com/GeyserMC/Floodgate)插件

## 安装

1. 从[发布页面](https://cnb.cool/EnderRealm/public/Easy4Form/-/releases)下载最新版本的Easy4Form
2. 将JAR文件放入服务器的`plugins`文件夹中
3. 重启服务器

## 使用方法（针对开发者）

要在您的项目中使用Easy4Form作为依赖项，您可以使用Maven或Gradle导入：

### Maven

将以下仓库添加到您的`pom.xml`中：

```xml
<repositories>
    <repository>
        <id>enderrealm-public</id>
        <url>https://maven.cnb.cool/EnderRealm/public/enderrealm-public-repo/-/packages/</url>
    </repository>
</repositories>
```

然后添加依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>cn.enderrealm</groupId>
        <artifactId>easy4form</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

将以下仓库添加到您的`build.gradle`中：

```gradle
repositories {
    maven {
        url 'https://maven.cnb.cool/EnderRealm/public/enderrealm-public-repo/-/packages/'
    }
}
```

然后添加依赖项：

```gradle
dependencies {
    compileOnly 'cn.enderrealm:easy4form:1.0-SNAPSHOT'
}
```

## 配置

Easy4Form现在包含一个配置文件（`config.yml`），具有以下选项：

```yaml
# 基岩版玩家检测模式 (floodgate, uuid)
detection-mode: "floodgate"

# 基岩版玩家的UUID前缀（仅在detection-mode设置为"uuid"时使用）
uuid-prefix: "00000000-0000"

# 调试模式 (true, false)
debug: false
```

### 检测模式

- **floodgate**：使用Floodgate API检测基岩版玩家（推荐）
- **uuid**：使用UUID前缀检测基岩版玩家（在Floodgate检测失败时有用）

您可以在`uuid`模式下自定义用于检测的UUID前缀。

## 特性

- **简化API**：易于使用的API，用于为基岩版玩家创建和发送表单
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

## 文档

有关详细文档和示例，请参阅[API文档](docs/api_zh.md)。

## 许可证

本项目采用MIT许可证 - 详情请参阅[LICENSE](LICENSE)文件。

## 贡献

请随时提交Pull Request。

## 致谢

- [Floodgate](https://github.com/GeyserMC/Floodgate) - 提供与基岩版玩家交互的API
- [Cumulus](https://github.com/GeyserMC/Cumulus) - Floodgate使用的表单库

## 支持

如果您遇到任何问题或有疑问，请在CNB上[提交问题](https://cnb.cool/EnderRealm/public/Easy4Form/-/issues)。

