# Easy4Form

**Language**: [English](README.md) | [中文](README_ZH.md)

A provider-based Form API making it easier to create and send forms from your Java server. Since 3.0.0 the library uses a public facade backed by internal form providers; Bedrock forms are sent through Floodgate and Cumulus, and Java players can receive native dialogs on Paper 1.21.7+ (disabled by default, opt-in).

## Introduction

Easy4Form is a Bukkit/Spigot plugin that provides a simplified interface for creating and sending forms. The bundled Cumulus provider sends forms to Bedrock players through the Floodgate API, and the Paper dialog provider sends native Paper 1.21.7 dialogs to Java players. It supports all three types of forms available in Bedrock Edition:

- **Simple Forms**: A list of buttons that the player can click
- **Modal Forms**: A dialog with two buttons (yes/no, confirm/cancel, etc.)
- **Custom Forms**: A form with various input elements (text fields, toggles, sliders, dropdowns, etc.)

## Requirements

- Minecraft server with Bukkit/Spigot/Paper 1.18+
- [Floodgate](https://github.com/GeyserMC/Floodgate) plugin for the Bedrock form provider (optional; without it Bedrock forms are disabled)
- Java dialogs require Paper 1.21.7+, Java 21, and a client on Minecraft 1.21.6+ (disabled by default)

Building this library requires JDK 21 (compiling to Java 17); the public Maven build is fully self-contained and does not need the internal Gradle setup.

## Installation

1. Download the latest version of Easy4Form from the [releases page](https://github.com/EnderRealmMC/Easy4Form/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server

## Usage (For Developers)

To use Easy4Form as a dependency in your project, you can import it using either Maven or Gradle:

### Maven

Add the following repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/EnderRealmMC/Easy4Form</url>
    </repository>
</repositories>
```

Then add the dependency:

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

Add the following repository to your `build.gradle`:

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

Then add the dependency:

```gradle
dependencies {
    compileOnly 'cn.enderrealm:easy4form:3.0.0'
}
```

## 3.0.0 Breaking Changes

- The public facade remains `cn.enderrealm.easy4form.api.*`; existing builder and `Easy4FormAPI` calls keep their signatures.
- `Easy4FormAPI.getFloodgatePlayer(...)` was removed.
- `cn.enderrealm.easy4form.api.PlayerUtils` moved to `cn.enderrealm.easy4form.utils.PlayerUtils`.
- `api.v1` and `api.v2` were removed; forms are now dispatched through an internal provider registry.
- Closed responses: simple and modal forms return `null`; custom forms return an empty map.

## Configuration

Easy4Form includes a configuration file (`config.yml`) with the following options:

```yaml
# One provider per platform; use none to disable forms for that platform
providers:
  bedrock-form: cumulus # cumulus | none
  java-dialog: none     # paper | none

# Detection mode for Bedrock players
# Options: floodgate, uuid
detection-mode: floodgate

# UUID prefix for Bedrock players (used in 'uuid' mode or as the fallback; requires Floodgate)
uuid-prefix: "00000000-0000"

# Debug mode
debug: false

# Java dialog text
java-dialog-text:
  submit: "Submit"
  close: "Close"
```

### Provider Selection

Each platform takes exactly one provider name from `config.yml`:

- `bedrock-form: cumulus` enables the Cumulus provider. It requires Floodgate; when Floodgate is missing, the provider is skipped automatically and Bedrock forms are disabled.
- `bedrock-form: none` disables Bedrock forms without disabling the plugin.
- `java-dialog: paper` enables the Paper dialog provider. It requires Paper 1.21.7+, Java 21, and the Paper Dialog API; otherwise it degrades to disabled and the plugin stays enabled.
- `java-dialog: none` disables Java dialogs (default).

Java dialogs are only sent to non-Bedrock players whose client protocol is at least 771 (Minecraft 1.21.6). When ViaVersion is installed, the real client version is read through the ViaAPI; if ViaVersion is present but the version is unavailable, no Java dialog is sent.

### Java Dialog Mapping and Limitations

- Simple forms map to a multi-button dialog; modal forms use two buttons so that closing can be distinguished from clicking "No".
- Every Java dialog has a footer close button, and custom forms add a submit button (text configurable via `java-dialog-text`).
- Paper has no dialog close event; the provider binds `exit_action` of `multi_action` to catch both Escape and the close button and maps them to the closed response (`null` for simple/modal, an empty map for custom).
- Unmappable features: button images, input placeholders, and interleaved label/input layout (labels are rendered together in the body).

### Detection Modes

Both modes require the Floodgate plugin to be installed. Cumulus ships with Floodgate, so when Floodgate is absent, Bedrock forms are unavailable and no player is treated as Bedrock.

- **floodgate**: Uses the Floodgate API to detect Bedrock players (recommended). Falls back to the UUID prefix when Floodgate is installed but not enabled or its API call fails.
- **uuid**: Uses a UUID prefix to detect Bedrock players. Also only active while Floodgate is installed.

You can customize the UUID prefix used for detection.

## Features

- **Simplified API**: Easy-to-use API for creating and sending forms
- **Configurable Providers**: Select the provider for each platform (`cumulus`, `paper`, or `none`); each platform degrades automatically when its dependency is unavailable
- **Fluent Interface**: All form builders use a fluent interface, allowing method chaining for cleaner code
- **Type Safety**: Response handlers provide type-safe access to form responses
- **Capability Query**: `Easy4FormAPI.isFormSupported(Player)` lets callers detect unsupported players and provide their own fallback
- **Comprehensive Documentation**: Detailed documentation with examples for all form types
- **Bilingual Support**: Documentation available in both English and Chinese

## Quick Examples

### Simple Form

```java
List<String> buttons = Arrays.asList("Button 1", "Button 2", "Button 3");
Easy4FormAPI.sendSimpleForm(player, "Title", "Content", buttons, response -> {
    if (response != null) {
        player.sendMessage("You clicked button: " + buttons.get(response));
    }
});
```

### Modal Form

```java
Easy4FormAPI.sendModalForm(
    player,
    "Confirmation",
    "Are you sure you want to proceed?",
    "Yes",
    "No",
    response -> {
        if (response != null && response) {
            player.sendMessage("You confirmed!");
        }
    }
);
```

### Custom Form

```java
CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "Settings", response -> {
    if (response != null) {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        player.sendMessage("Settings saved!");
    }
});

form.input("name", "Name", "Enter your name", player.getName())
    .toggle("notifications", "Enable Notifications", true)
    .send(player);
```



## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Credits

- [Floodgate](https://github.com/GeyserMC/Floodgate) - For providing the API to interact with Bedrock players
- [Cumulus](https://github.com/GeyserMC/Cumulus) - The form library used by Floodgate
- [Paper](https://github.com/PaperMC/Paper) - For the Java dialog API

## Support

If you encounter any issues or have questions, please [open an issue](https://github.com/EnderRealmMC/Easy4Form/issues) on GitHub.