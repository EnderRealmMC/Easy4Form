# Easy4Form

**Language**: [English](README.md) | [中文](README_ZH.md)

**Documentation**: [API Documentation v1](docs/v1/api.md) | [API 文档 v1](docs/v1/api_zh.md)

A simplified Form API for Floodgate, making it easier to create and send forms to Bedrock players from your Java server.

## Introduction

Easy4Form is a Bukkit/Spigot plugin that provides a simplified interface for creating and sending forms to Bedrock players through the Floodgate API. It supports all three types of forms available in Bedrock Edition:

- **Simple Forms**: A list of buttons that the player can click
- **Modal Forms**: A dialog with two buttons (yes/no, confirm/cancel, etc.)
- **Custom Forms**: A form with various input elements (text fields, toggles, sliders, dropdowns, etc.)

## Requirements

- Minecraft server with Bukkit/Spigot/Paper 1.18+
- [Floodgate](https://github.com/GeyserMC/Floodgate) plugin installed

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
        <version>1.0-SNAPSHOT</version>
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
    compileOnly 'cn.enderrealm:easy4form:1.0-SNAPSHOT'
}
```

## Configuration

Easy4Form now includes a configuration file (`config.yml`) with the following options:

```yaml
# Detection mode for Bedrock players
# Options: floodgate, uuid
detection-mode: floodgate

# UUID prefix for Bedrock players (only used when detection-mode is 'uuid')
uuid-prefix: "00000000-0000"

# Debug mode
debug: false

# API version (currently only v1 is supported)
api-version: "v1"

# Enable migration warnings for deprecated API usage
migration-warnings: true
```

### Detection Modes

- **floodgate**: Uses the Floodgate API to detect Bedrock players (recommended)
- **uuid**: Uses a UUID prefix to detect Bedrock players (useful when Floodgate detection fails)

You can customize the UUID prefix used for detection when in `uuid` mode.

## Features

- **Simplified API**: Easy-to-use API for creating and sending forms to Bedrock players
- **Fluent Interface**: All form builders use a fluent interface, allowing method chaining for cleaner code
- **Type Safety**: Response handlers provide type-safe access to form responses
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

## Support

If you encounter any issues or have questions, please [open an issue](https://github.com/EnderRealmMC/Easy4Form/issues) on GitHub.