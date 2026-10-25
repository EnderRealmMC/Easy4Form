# Easy4Form

**语言**: [English](README.md) | [中文](README_ZH.md)

面向 Minecraft 服务端的跨平台表单库。业务代码只写一次调用，基岩版玩家收到 Geyser 表单，Java 版玩家在支持的服务端上收到原生 Dialog。

## 它解决什么问题

服务端同时接纳 Java 玩家和 Geyser 玩家时，交互层是分裂的：基岩版有表单协议，Java 版没有；Java 版从 1.21.6 起有了原生 Dialog，语义又和基岩版表单不完全一致。Easy4Form 把这层差异收在一个门面后面——同一份标题、正文、按钮和输入组件，交给库去判断该给这个玩家发什么，回调参数也保持同一套语义。

三种表单类型：

- **简单表单**：一组按钮，返回被点击按钮的下标
- **模态表单**：两个按钮的确认框，返回是否点击了第一个按钮
- **自定义表单**：文本框、开关、滑块、步进滑块、下拉框等输入组件，返回组件 ID 到值的映射

## 版本要求

只声明 Paper。Spigot、Bukkit 等其他服务端我们从未实测过，不承诺行为一致。

| 目标 | 要求 |
|------|------|
| 最低可用 | Paper 1.18+（Java 17）；基岩版表单还需 Floodgate（随附 Cumulus） |
| 完整功能 | Paper 1.21.7+（Java 21）、玩家客户端 Minecraft 1.21.6+、`providers.java-dialog: paper`；基岩版侧仍需 Floodgate |
| 可选 | ViaVersion：安装后按真实客户端协议决定是否发送 Java 版 Dialog |

低于 Paper 1.21.7 的服务端上，Java 玩家不会收到任何表单，基岩版表单不受影响。插件本体以 Java 17 为目标编译，`plugin.yml` 的 `api-version` 为 `1.18`。

## 安装（服务端管理员）

1. 从 [Releases](https://github.com/EnderRealmMC/Easy4Form/releases) 下载 `easy4form-3.0.0.jar`
2. 放进服务端的 `plugins/` 目录
3. 重启服务端

Easy4Form 是库，不是会给玩家发菜单的插件，装完没有任何可感知变化；配置文件在首次启用时生成于 `plugins/easy4form/config.yml`。

Java 版 Dialog 默认关闭，需要显式打开：

```yaml
providers:
  bedrock-form: cumulus   # cumulus | none
  java-dialog: paper      # paper | none，默认 none
```

缺依赖时不会崩，只会降级：Floodgate 不在就不注册基岩版表单，服务端版本不够就注册不上 Java 版 Dialog，两种情况都只记一条告警日志，插件照常启用。

## 依赖它开发（插件作者）

Maven：

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/EnderRealmMC/Easy4Form</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>cn.enderrealm</groupId>
        <artifactId>easy4form</artifactId>
        <version>3.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle：

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

dependencies {
    compileOnly 'cn.enderrealm:easy4form:3.0.0'
}
```

在 `plugin.yml` 里声明 `depend: [easy4form]`（或 `softdepend`，配合下文的 `isFormSupported` 自行兜底）。

## 快速开始（Java）

### 简单表单

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

List<String> buttons = Arrays.asList("商店", "传送", "设置", "退出");

Easy4FormAPI.sendSimpleForm(player, "主菜单", "请选择一个选项", buttons, index -> {
    if (index == null) {
        player.sendMessage("你关闭了表单");
        return;
    }
    switch (index) {
        case 0: openShop(player); break;
        case 1: openTeleport(player); break;
        case 2: openSettings(player); break;
        default: player.sendMessage("再见！");
    }
});
```

按钮带图片（仅基岩版玩家可见，Java 版 Dialog 会忽略图片）：

```java
// 资源包路径
Easy4FormAPI.sendSimpleFormWithImages(
    player, "商店", "选择商品类别",
    Arrays.asList("武器", "防具", "食物"),
    Arrays.asList("textures/items/diamond_sword.png",
                  "textures/items/diamond_chestplate.png",
                  "textures/items/apple.png"),
    index -> { /* ... */ });

// URL 图片
Easy4FormAPI.sendSimpleFormWithUrlImages(
    player, "商店", "选择商品类别",
    Arrays.asList("武器", "防具", "食物"),
    Arrays.asList("https://example.com/sword.png",
                  "https://example.com/armor.png",
                  "https://example.com/food.png"),
    index -> { /* ... */ });
```

两个列表长度必须一致；某一项传 `null` 或空字符串时该按钮退化为纯文本。

### 模态表单

```java
Easy4FormAPI.sendModalForm(
    player,
    "确认删除",
    "确定要删除这个物品吗？此操作不可撤销。",
    "确定删除",   // 第一个按钮，回调收到 true
    "取消",       // 第二个按钮，回调收到 false
    confirmed -> {
        if (confirmed == null) {
            player.sendMessage("你关闭了对话框");
        } else if (confirmed) {
            deleteItem(player);
        }
    }
);
```

### 自定义表单

```java
Easy4FormAPI.createCustomForm(player, "个人设置", values -> {
    if (values.isEmpty()) {
        player.sendMessage("你关闭了设置");
        return;
    }
    String name = (String) values.get("player_name");
    boolean notifications = (boolean) values.get("notifications");
    int volume = ((Number) values.get("volume")).intValue();
    int language = (Integer) values.get("language");   // 选中项下标
    saveSettings(player, name, notifications, volume, language);
})
.input("player_name", "玩家名称", "输入你的名称", player.getName())
.toggle("notifications", "启用通知", true)
.slider("volume", "音量", 0f, 100f, 10f, 50f)
.dropdown("language", "语言", Arrays.asList("中文", "English", "日本語"), 0)
.send(player);
```

`createCustomForm` 在该玩家没有任何可用表单实现时返回 `null`，所以链式调用前需要判断，或者改用下面的 Builder。

### Builder 写法

三个 Builder 都有 `create()` 静态入口和无参构造，链式方法一致：

```java
new SimpleFormBuilder()
    .title("自定义菜单")
    .content("请选择操作")
    .button("选项1")
    .button("选项2", "textures/ui/icon")
    .buttonWithUrl("选项3", "https://example.com/a.png")
    .responseHandler(index -> { /* ... */ })
    .send(player);

new ModalFormBuilder()
    .title("确认传送")
    .content("即将传送到目标世界")
    .button1("立即传送")
    .button2("取消")
    .responseHandler(confirmed -> { /* ... */ })
    .send(player);
```

## 快速开始（Kotlin DSL）

同一套门面之上加了扩展函数与尾随 lambda，逐按钮回调直接写在按钮上。DSL 只是语法糖，能力不超出上面的 Java 接口。

```kotlin
import cn.enderrealm.easy4form.api.dsl.customForm
import cn.enderrealm.easy4form.api.dsl.modalForm
import cn.enderrealm.easy4form.api.dsl.simpleForm

player.simpleForm("主菜单", "请选择一个操作") {
    button("开始游戏") { /* 这个按钮被点击 */ }
    button("资源包图标", "textures/ui/icon") { }
    buttonWithUrl("网页按钮", "https://example.com/a.png") { }
    closed { /* 玩家关闭表单，等价于 Java 侧收到 null */ }
}

player.modalForm("确认退出", "确定要离开吗？", "确定", "取消") {
    confirm { }
    cancel { }
    closed { }
}

player.customForm("个人资料") {
    label("填写你的资料")
    input("name", "昵称", "请输入昵称", "Steve")
    toggle("public", "公开资料", true)
    slider("volume", "音量", 0f, 100f)                 // step 默认 1f，默认值默认取最小值
    stepSlider("rank", "段位", "青铜", "白银", "王者", defaultStep = 1)
    dropdown("language", "语言", "zh_CN", "en_US")
    submitted { values ->
        values.string("name")       // input
        values.boolean("public")    // toggle
        values.float("volume")      // slider
        values.intIndex("rank")     // stepSlider / dropdown 的选中下标
    }
    closed { /* Java 侧的空 Map 折叠到这里 */ }
}
```

`FormValues` 还提供 `keys`、`operator get`；取值失败返回 `null`，不抛异常。

Kotlin 侧注意两点：编译器需要 Kotlin 1.9 或更新；依赖坐标使用主产物（`provided` / `compileOnly`），不要再把 kotlin-stdlib 打进你自己的插件——整台服务器只能存在一份 stdlib 定义，否则跨插件传递的 lambda 会在运行期类型转换失败。

## API 一览

| 入口 | 说明 |
|------|------|
| `Easy4FormAPI.sendSimpleForm(...)` | 发送按钮列表表单，回调参数为按钮下标 |
| `Easy4FormAPI.sendSimpleFormWithImages(...)` | 资源包图片按钮，仅基岩版生效 |
| `Easy4FormAPI.sendSimpleFormWithUrlImages(...)` | URL 图片按钮，仅基岩版生效 |
| `Easy4FormAPI.sendModalForm(...)` | 发送两按钮确认框 |
| `Easy4FormAPI.createCustomForm(...)` | 返回自定义表单构建器，无可用表单实现时返回 `null` |
| `Easy4FormAPI.isFormSupported(player)` | 该玩家当前能否收到表单 |
| `Easy4FormAPI.isBedrockPlayer(player \| uuid)` | 基岩版玩家判定 |
| `SimpleFormBuilder` / `ModalFormBuilder` / `CustomFormBuilder` | 链式构造，`send(player)` 提交 |
| `api.dsl` 的 `simpleForm` / `modalForm` / `customForm` | Kotlin 扩展函数 |
| `cn.enderrealm.easy4form.utils.PlayerUtils` | 底层检测工具 |

## 自定义表单组件

| 组件 | 方法 | 回调里的值 |
|------|------|------------|
| 标签 | `label(id, text)`、`label(text)` | `null`（仅展示，自动 ID 为 `label_<序号>`） |
| 文本框 | `input(id, text, placeholder, defaultValue)` | `String` |
| 开关 | `toggle(id, text, defaultValue)` | `Boolean` |
| 滑块 | `slider(id, text, min, max, step, defaultValue)` | `Float` |
| 步进滑块 | `stepSlider(id, text, steps, defaultStep)` | `Integer` 选中下标 |
| 下拉框 | `dropdown(id, text, options, defaultOption)` | `Integer` 选中下标 |

标签同样占用一个组件下标：`label` 也会出现在响应里（值为 `null`），按下标取值时要把标签算进去；用 ID 取值则不受影响。

## 响应与关闭语义

| 表单类型 | 正常响应 | 关闭或响应非法 |
|----------|----------|----------------|
| 简单表单 | 按钮下标 `Integer` | `null` |
| 模态表单 | 是否点击第一个按钮 `Boolean` | `null` |
| 自定义表单 | ID 到值的 `Map` | 空 `Map`（不是 `null`） |

没有可用表单实现时发送会被静默跳过，回调不会触发——不会抛异常，也不会回一个「关闭」。需要兜底交互就先查 `isFormSupported`：

```java
if (Easy4FormAPI.isFormSupported(player)) {
    Easy4FormAPI.sendSimpleForm(player, "主菜单", "请选择", buttons, index -> { /* ... */ });
} else {
    player.sendMessage("当前客户端不支持表单，请使用 /menu 命令");
}
```

## 配置项

`plugins/easy4form/config.yml`：

| 键 | 可选值 | 默认 | 说明 |
|----|--------|------|------|
| `providers.bedrock-form` | `cumulus` / `none` | `cumulus` | 基岩版表单；缺 Floodgate 时自动降级为 `none` |
| `providers.java-dialog` | `paper` / `none` | `none` | Java 版原生 Dialog；需要 Paper 1.21.7+ 与 Java 21 |
| `detection-mode` | `floodgate` / `uuid` | `floodgate` | 基岩版玩家判定方式；两种都要求已安装 Floodgate |
| `uuid-prefix` | 字符串 | `00000000-0000` | `uuid` 模式与兜底路径使用的前缀 |
| `java-dialog-text.submit` | 字符串 | `Submit` | 自定义表单的提交按钮文本，支持 `§` 颜色码 |
| `java-dialog-text.close` | 字符串 | `Close` | 页脚关闭按钮文本，同时接管 Escape，支持 `§` 颜色码 |
| `debug` | 布尔 | `false` | 打开详细日志 |

## 已知限制

- 按钮图片（资源包与 URL）只对基岩版玩家生效，Java 版 Dialog 没有对应能力，会被忽略
- 文本框占位符只在基岩版生效
- 自定义表单里的 `label` 在 Java 版 Dialog 上统一显示在正文区域，无法与输入控件交错排列
- Java 版文本组件在按钮标签、标题、下拉选项等单行表面**不渲染换行符**（原版渲染限制），保留 `\n` 会显示为占位字符；库会把按钮、标题与下拉选项中的 `\n` 清洗为空格，正文与 `label` 会按 `\n` 拆行渲染。控件标签请保持单行简短
- Java 版下拉（`single_option`）没有独立标题：库将标题文本前置为正文行，控件内仅显示选中项；基岩下拉的标题+说明长文本在 Java 端建议精简
- 自定义表单里控件之后的纯文本通过只读单行文本框占位保序，视觉上带输入框边框，与基岩纯文本略有差异
- Java 版 Dialog 没有「关闭」事件，协议里也没有客户端到服务端的关闭包；库通过 Dialog 的退出动作把 Escape 与页脚关闭按钮归一为关闭响应，这依赖原版 Dialog 语义
- Java 版 Dialog 的响应来自客户端提交的自定义点击负载，可能被伪造；金额、数量、选项等敏感值必须在服务端重新校验
- 表单响应是异步回调，回调里不要执行阻塞操作

## 从源码构建

需要 JDK 21（产物仍以 Java 17 为目标）：

```bash
mvn clean package
```

产物有两个：`easy4form-3.0.0-all.jar` 内置 kotlin-stdlib，是放进 `plugins/` 的那一个（Release 页面把它按 `easy4form-3.0.0.jar` 的名字上传）；`easy4form-3.0.0.jar` 只含本库类，供编译依赖使用。

## 文档

- 更新日志：[release.md](release.md)
- 对外承诺只覆盖 `cn.enderrealm.easy4form.api` 与 `cn.enderrealm.easy4form.api.dsl`；`internal` 包随时可能变动，不要直接引用

## 许可证

MIT License，详见 [LICENSE](LICENSE)。

## 致谢

- [Floodgate](https://github.com/GeyserMC/Floodgate) 与 [Cumulus](https://github.com/GeyserMC/Cumulus)：基岩版表单通道
- [Paper](https://github.com/PaperMC/Paper)：Java 版 Dialog API
- [ViaVersion](https://github.com/ViaVersion/ViaVersion)：跨版本服务端下的真实客户端协议来源
- [EaseCation/ViaBedrock](https://github.com/EaseCation/ViaBedrock)：Java Dialog 表单转换参考了其 `BedrockFormDialogConverter` 的部分思路（正文按行拆分、纯文本占位保序、按钮宽度与空选项契约）；本项目未复制其代码，实现基于 Paper API 独立编写

## 支持

遇到问题或有功能建议，请在 GitHub 上[提交 Issue](https://github.com/EnderRealmMC/Easy4Form/issues)。
