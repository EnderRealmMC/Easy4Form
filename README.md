# Easy4Form

**Language**: [English](README.md) | [中文](README_ZH.md)

A cross-platform form library for Minecraft servers. Write the call once: Bedrock players get a Geyser form, Java players get a native dialog on servers that support it.

## What it solves

Servers that accept both Java and Geyser players end up with two different interaction layers. Bedrock has a form protocol, Java does not; Java gained native dialogs in 1.21.6, but their semantics do not line up with Bedrock forms either. Easy4Form hides that split behind one facade — you build a single title, body, button and input definition, and the library decides what this particular player can actually receive, keeping the callback semantics identical on both sides.

Three form types:

- **Simple form**: a list of buttons, callback receives the clicked button index
- **Modal form**: a two-button confirmation, callback receives whether the first button was clicked
- **Custom form**: input elements (text field, toggle, slider, step slider, dropdown), callback receives an id-to-value map

## Requirements

We only claim Paper. Spigot, plain Bukkit and other servers have never been tested here, so no behaviour is promised on them.

| Goal | Requirements |
|------|--------------|
| Minimum working setup | Paper 1.18+ (Java 17); Bedrock forms additionally need Floodgate (bundles Cumulus) |
| Full feature set | Paper 1.21.7+ (Java 21), client on Minecraft 1.21.6+, `providers.java-dialog: paper`; Bedrock side still needs Floodgate |
| Optional | ViaVersion — when present, the real client protocol decides whether Java dialogs are sent |

On servers older than Paper 1.21.7 Java players simply receive nothing, while Bedrock forms keep working. The plugin is compiled against Java 17 and declares `api-version: 1.18`.

## Installation (server owners)

1. Download `easy4form-3.0.0.jar` from [Releases](https://github.com/EnderRealmMC/Easy4Form/releases)
2. Drop it into the server's `plugins/` folder
3. Restart the server

On first start the kotlin-stdlib declared in `plugin.yml` via `libraries` is downloaded automatically from Maven Central (about 1.5 MB, cached in the server's `libraries/` folder, downloaded only once); the server must be able to reach Maven Central.

Easy4Form is a library, not a plugin that hands out menus, so installing it changes nothing visible. The configuration file is created on first start at `plugins/easy4form/config.yml`.

Java dialogs are off by default and must be enabled explicitly:

```yaml
providers:
  bedrock-form: cumulus   # cumulus | none
  java-dialog: paper      # paper | none, defaults to none
```

Missing dependencies degrade instead of crashing: without Floodgate no Bedrock form channel is registered, and on an unsupported server the Java dialog channel cannot be registered. Both cases log a single warning and the plugin still enables.

## Using it as a dependency (plugin authors)

Maven:

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

Gradle:

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

Declare `depend: [easy4form]` in your `plugin.yml` (or `softdepend` and fall back yourself with `isFormSupported`, below).

## Quick start (Java)

### Simple form

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

List<String> buttons = Arrays.asList("Shop", "Teleport", "Settings", "Exit");

Easy4FormAPI.sendSimpleForm(player, "Main menu", "Pick an option", buttons, index -> {
    if (index == null) {
        player.sendMessage("You closed the form");
        return;
    }
    switch (index) {
        case 0: openShop(player); break;
        case 1: openTeleport(player); break;
        case 2: openSettings(player); break;
        default: player.sendMessage("Bye!");
    }
});
```

Buttons with images (Bedrock players only — Java dialogs have no equivalent, so images are dropped):

```java
// resource pack path
Easy4FormAPI.sendSimpleFormWithImages(
    player, "Shop", "Choose a category",
    Arrays.asList("Weapons", "Armour", "Food"),
    Arrays.asList("textures/items/diamond_sword.png",
                  "textures/items/diamond_chestplate.png",
                  "textures/items/apple.png"),
    index -> { /* ... */ });

// URL images
Easy4FormAPI.sendSimpleFormWithUrlImages(
    player, "Shop", "Choose a category",
    Arrays.asList("Weapons", "Armour", "Food"),
    Arrays.asList("https://example.com/sword.png",
                  "https://example.com/armor.png",
                  "https://example.com/food.png"),
    index -> { /* ... */ });
```

Both lists must be the same size; a `null` or empty entry makes that button text-only.

### Modal form

```java
Easy4FormAPI.sendModalForm(
    player,
    "Confirm delete",
    "Delete this item? This cannot be undone.",
    "Delete",   // first button, callback receives true
    "Cancel",   // second button, callback receives false
    confirmed -> {
        if (confirmed == null) {
            player.sendMessage("You closed the dialog");
        } else if (confirmed) {
            deleteItem(player);
        }
    }
);
```

### Custom form

```java
Easy4FormAPI.createCustomForm(player, "Settings", values -> {
    if (values.isEmpty()) {
        player.sendMessage("You closed the settings");
        return;
    }
    String name = (String) values.get("player_name");
    boolean notifications = (boolean) values.get("notifications");
    int volume = ((Number) values.get("volume")).intValue();
    int language = (Integer) values.get("language");   // selected option index
    saveSettings(player, name, notifications, volume, language);
})
.input("player_name", "Player name", "Type your name", player.getName())
.toggle("notifications", "Enable notifications", true)
.slider("volume", "Volume", 0f, 100f, 10f, 50f)
.dropdown("language", "Language", Arrays.asList("中文", "English", "日本語"), 0)
.send(player);
```

`createCustomForm` returns `null` when the player has no usable form channel, so check before chaining, or use a builder instead.

### Builder style

All three builders expose `create()` plus a public no-arg constructor and chain the same way:

```java
new SimpleFormBuilder()
    .title("Custom menu")
    .content("Pick an action")
    .button("Option 1")
    .button("Option 2", "textures/ui/icon")
    .buttonWithUrl("Option 3", "https://example.com/a.png")
    .responseHandler(index -> { /* ... */ })
    .send(player);

new ModalFormBuilder()
    .title("Confirm teleport")
    .content("You are about to travel to another world")
    .button1("Teleport now")
    .button2("Cancel")
    .responseHandler(confirmed -> { /* ... */ })
    .send(player);
```

## Quick start (Kotlin DSL)

Extension functions and trailing lambdas on top of the same facade, with per-button callbacks written next to the button. The DSL is sugar only — it offers nothing beyond the Java API above.

```kotlin
import cn.enderrealm.easy4form.api.dsl.customForm
import cn.enderrealm.easy4form.api.dsl.modalForm
import cn.enderrealm.easy4form.api.dsl.simpleForm

player.simpleForm("Main menu", "Pick an action") {
    button("Start game") { /* this button was clicked */ }
    button("Resource pack icon", "textures/ui/icon") { }
    buttonWithUrl("Web button", "https://example.com/a.png") { }
    closed { /* the player closed the form — the Java side would get null */ }
}

player.modalForm("Confirm exit", "Leave now?", "Yes", "No") {
    confirm { }
    cancel { }
    closed { }
}

player.customForm("Profile") {
    label("Fill in your details")
    input("name", "Nickname", "Type a nickname", "Steve")
    toggle("public", "Public profile", true)
    slider("volume", "Volume", 0f, 100f)                  // step defaults to 1f, default value defaults to min
    stepSlider("rank", "Rank", "Bronze", "Silver", "Royal", defaultStep = 1)
    dropdown("language", "Language", "zh_CN", "en_US")
    submitted { values ->
        values.string("name")       // input
        values.boolean("public")    // toggle
        values.float("volume")      // slider
        values.intIndex("rank")     // selected index for stepSlider / dropdown
    }
    closed { /* the empty Map from the Java facade lands here */ }
}
```

`FormValues` also exposes `keys` and `operator get`; every accessor returns `null` instead of throwing.

Two Kotlin specifics: the compiler must be Kotlin 1.9 or newer, and depend on the main artifact (`provided` / `compileOnly`). If your plugin uses Kotlin, declare your own kotlin-stdlib via the `libraries` entry in your `plugin.yml`; cross-plugin callbacks go through `fun interface` (plain Java interfaces), so each plugin can keep its own stdlib version without class-loader cast errors.

## API overview

| Entry point | Purpose |
|-------------|---------|
| `Easy4FormAPI.sendSimpleForm(...)` | Send a button list, callback receives the index |
| `Easy4FormAPI.sendSimpleFormWithImages(...)` | Resource pack button images, Bedrock only |
| `Easy4FormAPI.sendSimpleFormWithUrlImages(...)` | URL button images, Bedrock only |
| `Easy4FormAPI.sendModalForm(...)` | Send a two-button confirmation |
| `Easy4FormAPI.createCustomForm(...)` | Returns a custom form builder, or `null` when no form channel is available |
| `Easy4FormAPI.isFormSupported(player)` | Whether this player can receive a form at all |
| `Easy4FormAPI.isBedrockPlayer(player \| uuid)` | Bedrock player detection |
| `SimpleFormBuilder` / `ModalFormBuilder` / `CustomFormBuilder` | Chainable construction, submitted with `send(player)` |
| `simpleForm` / `modalForm` / `customForm` in `api.dsl` | Kotlin extension functions |
| `cn.enderrealm.easy4form.utils.PlayerUtils` | Low-level detection helper |

## Custom form components

| Component | Method | Value in the callback |
|-----------|--------|-----------------------|
| Label | `label(id, text)`, `label(text)` | `null` (display only; auto id is `label_<index>`) |
| Text field | `input(id, text, placeholder, defaultValue)` | `String` |
| Toggle | `toggle(id, text, defaultValue)` | `Boolean` |
| Slider | `slider(id, text, min, max, step, defaultValue)` | `Float` |
| Step slider | `stepSlider(id, text, steps, defaultStep)` | `Integer` selected index |
| Dropdown | `dropdown(id, text, options, defaultOption)` | `Integer` selected index |

Labels also consume a component index: they appear in the response (as `null`), so counting by index must include them. Reading by id is unaffected.

## Response and close semantics

| Form type | Normal response | Closed or invalid |
|-----------|-----------------|-------------------|
| Simple | Button index `Integer` | `null` |
| Modal | Whether the first button was clicked `Boolean` | `null` |
| Custom | Id-to-value `Map` | Empty `Map` (never `null`) |

When no form channel serves the player, sending is silently skipped — no exception, and no "closed" callback either. Check before offering a fallback:

```java
if (Easy4FormAPI.isFormSupported(player)) {
    Easy4FormAPI.sendSimpleForm(player, "Main menu", "Pick an option", buttons, index -> { /* ... */ });
} else {
    player.sendMessage("Your client cannot open forms, use /menu instead");
}
```

## Configuration

`plugins/easy4form/config.yml`:

| Key | Values | Default | Meaning |
|-----|--------|---------|---------|
| `providers.bedrock-form` | `cumulus` / `none` | `cumulus` | Bedrock forms; automatically falls back to `none` without Floodgate |
| `providers.java-dialog` | `paper` / `none` | `none` | Native Java dialogs; requires Paper 1.21.7+ and Java 21 |
| `detection-mode` | `floodgate` / `uuid` | `floodgate` | How Bedrock players are detected; both modes require Floodgate to be installed |
| `uuid-prefix` | string | `00000000-0000` | Prefix used by `uuid` mode and by the fallback path |
| `java-dialog-text.submit-key` | string | `gui.done` | Vanilla translation key for the custom form submit button; when non-empty the client resolves it against its own language file, automatically covering every Minecraft language with zero maintenance. Empty falls back to `submit` below |
| `java-dialog-text.close-key` | string | `gui.cancel` | Vanilla translation key for the footer close button (also handles Escape); resolved by the client in its own language when non-empty. Empty falls back to `close` below |
| `java-dialog-text.submit` | string | `Submit` | Fixed submit button text used when the translation key is empty, supports `§` colour codes |
| `java-dialog-text.close` | string | `Close` | Fixed close button text used when the translation key is empty, supports `§` colour codes |
| `debug` | boolean | `false` | Verbose logging |

## Known limitations

- Button images (resource pack and URL) only reach Bedrock players; Java dialogs have no equivalent and drop them
- Input placeholders only apply on Bedrock
- `label` elements of a custom form render as body lines when placed before the first input; ones placed after an input are rendered as read-only single-line text boxes to preserve ordering (see below)
- Java text components **do not render newlines** in single-line surfaces such as button labels, titles and dropdown options (vanilla rendering limit); a literal `\n` would show as a placeholder glyph. The library strips `\n` from buttons, titles and option entries, and splits bodies and `label` texts into per-line entries. Keep control labels short and single-line
- Java dialogs (`single_option`) have no separate title: the library moves the title text into the body lines and shows only the selected entry inside the control; keep Bedrock dropdown titles and descriptions short for Java rendering
- Plain text placed after an input of a custom form is rendered as a read-only single-line text box to preserve ordering, which carries an input border unlike plain Bedrock text
- Paper has no dialog close event and the protocol has no client-to-server close packet; the library normalises Escape and the footer close button into a closed response through the dialog's exit action, which relies on vanilla dialog behaviour
- Java dialog responses come from a client-supplied custom click payload and can be forged — re-validate amounts, quantities and option indexes on the server
- Callbacks arrive on the backing provider's thread: the main server thread for Java dialogs, Floodgate network threads for Bedrock forms. Dispatch to the main thread yourself when needed, and never block inside a callback

## Building from source

JDK 21 is required (the output still targets Java 17):

```bash
mvn clean package
```

A single artifact is produced: `easy4form-3.0.0.jar` (only this library's classes), used both as the compile-time dependency and for deployment in `plugins/`; kotlin-stdlib is downloaded at runtime via the `libraries` entry in `plugin.yml` (see Installation).

## Documentation

- Changelog: [release.md](release.md)
- The public contract covers `cn.enderrealm.easy4form.api` and `cn.enderrealm.easy4form.api.dsl` only; `internal` may change at any time, so do not depend on it

## License

MIT License — see [LICENSE](LICENSE).

## Credits

- [Floodgate](https://github.com/GeyserMC/Floodgate) and [Cumulus](https://github.com/GeyserMC/Cumulus) — the Bedrock form channel
- [Paper](https://github.com/PaperMC/Paper) — the Java dialog API
- [ViaVersion](https://github.com/ViaVersion/ViaVersion) — real client protocol on transVersion servers
- [EaseCation/ViaBedrock](https://github.com/EaseCation/ViaBedrock) — the Java dialog form conversion follows the ideas of its `BedrockFormDialogConverter` (per-line body splitting, placeholder text inputs for ordering, button width and the empty-option contract) and is rewritten against the Paper API; no code was copied — this project remains MIT-licensed

## Support

Bugs and feature requests: [open an issue](https://github.com/EnderRealmMC/Easy4Form/issues).
