# Easy4Form

**语言**: [English](README.md) | [中文](README_ZH.md)

基于 Provider 的简化 Form 接口，使得在 Java 服务器上创建和发送表单变得更加简单。自 3.0.0 起，库采用公开门面加内部表单 Provider 的架构；基岩版表单通过 Floodgate 与 Cumulus 发送，Java 版玩家可在 Paper 1.21.7+ 上接收原生 Dialog（默认关闭，需显式启用）。门面之上另有 `api/dsl`，为 Kotlin 开发者提供扩展函数与尾随 lambda 写法。

## 简介

Easy4Form是一个Bukkit/Spigot插件，提供了一个简化的接口，用于创建和发送表单。内置的 Cumulus Provider 通过 Floodgate API 为基岩版玩家发送表单；Paper Dialog Provider 为 Java 版玩家发送 Paper 1.21.7 的原生 Dialog。它支持基岩版中可用的所有三种表单类型：

- **简单表单**：玩家可以点击的按钮列表
- **模态表单**：带有两个按钮的对话框（是/否，确认/取消等）
- **自定义表单**：带有各种输入元素的表单（文本框，开关，滑块，下拉菜单等）

## 要求

- 运行Bukkit/Spigot/Paper 1.18+的Minecraft服务器
- [Floodgate](https://github.com/GeyserMC/Floodgate)插件（基岩版表单 Provider 需要；缺失时自动禁用基岩版表单）
- Java 版 Dialog 需要 Paper 1.21.7+ 与 Java 21，且玩家客户端为 Minecraft 1.21.6+（默认不启用）

构建本库需要 JDK 21（编译目标为 Java 17），公共 Maven 构建即可完成，无需访问内部 Gradle。使用 Kotlin DSL 的项目需要 Kotlin 1.9 或更高版本的编译器。

## 安装

1. 从[发布页面](https://github.com/EnderRealmMC/Easy4Form/releases)下载 `easy4form-3.0.0.jar`（已内置 Kotlin 标准库，服务器只需要这一个文件）
2. 将JAR文件放入服务器的`plugins`文件夹中
3. 重启服务器

自行构建（`mvn clean package`）会得到两个 jar：`target/easy4form-3.0.0-all.jar` 内置 Kotlin 标准库，发布页面上传的就是它（改名为 `easy4form-3.0.0.jar`）；`target/easy4form-3.0.0.jar` 只含本库类，供项目编译依赖使用，不要单独部署到服务器。

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

Kotlin 项目请注意两点：依赖坐标保持默认（主产物不含 Kotlin 标准库，标准库由您项目自己的 Kotlin 插件提供），并把 `plugin.yml` 的 `depend` 或 `paper-plugin.yml` 的依赖指向 `easy4form`。请不要在您的插件里再打包一份 `kotlin-stdlib`——DSL 的配置块类型需要全服务器只解析到一份标准库定义，两份副本会导致运行期 `ClassCastException`。

## 3.0.0 破坏性变更

- 公开门面仍为 `cn.enderrealm.easy4form.api.*`，现有构建器与 `Easy4FormAPI` 调用签名不变。
- 移除 `Easy4FormAPI.getFloodgatePlayer(...)`。
- `cn.enderrealm.easy4form.api.PlayerUtils` 迁移至 `cn.enderrealm.easy4form.utils.PlayerUtils`。
- 删除 `api.v1` 与 `api.v2`，表单改由内部 Provider 注册表分发。
- 表单关闭响应：简单表单与模态表单返回 `null`，自定义表单返回空 Map。
- 部署产物现在内置 Kotlin 标准库，体积明显增大（构建输出为 `*-all.jar`，发布页面以 `easy4form-3.0.0.jar` 名称上传）；Java 调用方式与依赖坐标不变，编译仍使用不含标准库的主产物。

## 配置

Easy4Form包含一个配置文件（`config.yml`），具有以下选项：

```yaml
# 每个平台只能选择一个 Provider，none 表示禁用该平台表单
providers:
  bedrock-form: cumulus # cumulus | none
  java-dialog: none     # paper | none

# 基岩版玩家检测模式
# 选项: floodgate（使用Floodgate API）, uuid（使用UUID前缀）
detection-mode: floodgate

# 基岩版玩家的UUID前缀（'uuid'模式或兜底时使用；要求 Floodgate 已安装）
uuid-prefix: "00000000-0000"

# 调试模式
debug: false

# Java 版 Dialog 文本
java-dialog-text:
  submit: "Submit"
  close: "Close"
```

### Provider 选择

每个平台从 `config.yml` 中选择唯一的 Provider：

- `bedrock-form: cumulus` 启用 Cumulus Provider。它依赖 Floodgate；Floodgate 缺失时会自动跳过并禁用基岩版表单。
- `bedrock-form: none` 禁用基岩版表单，但不影响插件启用。
- `java-dialog: paper` 启用 Paper Dialog Provider，需要 Paper 1.21.7+、Java 21 与 Paper Dialog API；不满足时自动降级为禁用，插件仍正常启用。
- `java-dialog: none` 禁用 Java 版 Dialog（默认）。

Java 版 Dialog 只对非基岩版且客户端协议 ≥ 771（Minecraft 1.21.6）的玩家发送。安装 ViaVersion 时会通过 ViaAPI 读取真实客户端版本；ViaVersion 存在但版本信息不可用时不做发送。

### Java 版 Dialog 的映射与限制

- 简单表单映射为多按钮 Dialog；模态表单用两个按钮实现，以便区分“关闭”与点击“否”。
- 所有 Java Dialog 都带一个页脚关闭按钮，自定义表单额外带提交按钮（文本可用 `java-dialog-text` 配置）。
- Paper 没有 Dialog 关闭事件；Provider 通过 `multi_action` 的 `exit_action` 捕获 Escape 与关闭按钮，映射为关闭响应（简单/模态为 `null`，自定义为空 Map）。
- 无法映射的能力：按钮图片、输入框占位符、label 与输入控件交错布局（label 统一显示在正文区域）。

### 检测模式

两种模式都要求已安装 Floodgate。Cumulus 随 Floodgate 一同提供，因此 Floodgate 不存在时基岩版表单能力不可用，任何玩家都不会被识别为基岩版玩家。

- **floodgate**：使用 Floodgate API 检测基岩版玩家（推荐）。Floodgate 已安装但未启用或 API 调用失败时回退到 UUID 前缀检测。
- **uuid**：使用 UUID 前缀检测基岩版玩家，同样仅在 Floodgate 已安装时生效。

您可以自定义用于检测的 UUID 前缀。

## 特性

- **简化API**：易于使用的API，用于创建和发送表单
- **可配置 Provider**：按平台选择 Provider（`cumulus` / `paper` / `none`）；依赖不可用时对应平台自动降级
- **流畅接口**：所有表单构建器都使用流畅的接口，允许方法链接以获得更清晰的代码
- **Kotlin DSL**：`api/dsl` 提供 `player.simpleForm` / `modalForm` / `customForm` 扩展函数，支持逐按钮回调与类型化取值
- **类型安全**：响应处理器提供对表单响应的类型安全访问
- **能力查询**：`Easy4FormAPI.isFormSupported(Player)` 可在发送前判断玩家是否可接收表单，便于实现兜底交互
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

### Kotlin DSL

Kotlin 开发者用扩展函数与尾随 lambda 写同一套表单，关闭与非法响应统一进入 `closed`：

```kotlin
import cn.enderrealm.easy4form.api.dsl.customForm
import cn.enderrealm.easy4form.api.dsl.modalForm
import cn.enderrealm.easy4form.api.dsl.simpleForm

player.simpleForm("主菜单", "请选择一个操作") {
    button("开始游戏") { index -> player.sendMessage("你点击了第 $index 个按钮") }
    button("资源包图标", "textures/ui/icon") { }
    buttonWithUrl("网页按钮", "https://example.com/a.png") { }
    closed { player.sendMessage("已关闭") }
}

player.modalForm("确认", "您确定要继续吗？", "是", "否") {
    confirm { player.sendMessage("您已确认！") }
    cancel { }
    closed { }
}

player.customForm("设置") {
    label("以下设置会立即生效")
    input("name", "名称", "输入您的名称", player.name)
    toggle("notifications", "启用通知", true)
    slider("volume", "音量", 0f, 100f)
    stepSlider("rank", "段位", "青铜", "王者", defaultStep = 1)
    dropdown("language", "语言", "zh_CN", "en_US")
    submitted { values ->
        val name = values.string("name")
        val notifications = values.boolean("notifications")
        val volume = values.float("volume")
        val rank = values.intIndex("rank")
    }
    closed { player.sendMessage("未提交") }
}
```

`label` 与 Java 构建器使用同一套自动 ID 规则，并且同样占用一个组件下标。没有可用 Provider 时发送静默跳过，`closed` 也不会触发；需要兜底交互时先用 `Easy4FormAPI.isFormSupported(player)` 判断。



## 许可证

本项目采用MIT许可证 - 详情请参阅[LICENSE](LICENSE)文件。

## 贡献

请随时提交Pull Request。

## 致谢

- [Floodgate](https://github.com/GeyserMC/Floodgate) - 提供与基岩版玩家交互的API
- [Cumulus](https://github.com/GeyserMC/Cumulus) - Floodgate使用的表单库
- [Paper](https://github.com/PaperMC/Paper) - 提供 Java 版 Dialog API

## 支持

如果您遇到任何问题或有疑问，请在 GitHub 上[提交问题](https://github.com/EnderRealmMC/Easy4Form/issues)。

