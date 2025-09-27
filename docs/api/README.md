# Easy4Form Main Proxy API Documentation

> **✅ Recommended Usage**
> 
> This is the **Main Proxy API** of Easy4Form and is the **recommended way to use** the library. It automatically selects the best underlying implementation (v1 or v2), providing optimal compatibility and performance.
> 
> - **Automatic Compatibility**: Automatically selects the appropriate API version based on server environment
> - **Best Performance**: Prioritizes v2 API, falls back to v1 when unavailable
> - **Simplified Development**: No need to worry about underlying implementation details
> - **Future-Proof**: Automatically benefits from improvements with new releases

**Language**: [English](README.md) | [中文](README_ZH.md)

**Back to**: [Main Documentation](../README.md)

The Easy4Form Main Proxy API is the **recommended** way to use Easy4Form. It provides automatic version detection and compatibility handling, ensuring your code works seamlessly across different Cumulus versions.

## Table of Contents

- [Why Use the Main Proxy API?](#why-use-the-main-proxy-api)
- [Getting Started](#getting-started)
- [Form Types](#form-types)
  - [Simple Forms](simple-forms.md)
  - [Modal Forms](modal-forms.md)
  - [Custom Forms](custom-forms.md)
- [Player Utilities](#player-utilities)
- [Advanced Features](#advanced-features)
- [Migration Guide](#migration-guide)

## Why Use the Main Proxy API?

The Main Proxy API offers several key advantages:

### **Automatic Version Management**
- Automatically detects the available Cumulus version
- Routes calls to the appropriate implementation (v1 or v2)
- No need to worry about compatibility issues

### **Future-Proof**
- Your code remains compatible as Easy4Form evolves
- Seamless upgrades without code changes
- Protection against breaking changes

### **Best Performance**
- Uses the most optimized implementation available
- Automatic fallback to stable versions when needed
- Minimal overhead from proxy layer

### **Unified API**
- Single import for all functionality
- Consistent method signatures across versions
- Simplified development experience

### **Easy Maintenance**
- Centralized error handling
- Comprehensive logging and debugging
- Active support and updates

## Getting Started

### Installation

Add Easy4Form to your project dependencies:

```xml
<dependency>
    <groupId>cn.enderrealm</groupId>
    <artifactId>easy4form</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Basic Usage

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import org.bukkit.entity.Player;

// The proxy automatically handles version detection and routing
public class MyPlugin {
    
    public void sendWelcomeForm(Player player) {
        List<String> buttons = Arrays.asList("Play", "Settings", "Quit");
        
        Easy4FormAPI.sendSimpleForm(
            player,
            "Welcome!",
            "Choose an option:",
            buttons,
            response -> {
                if (response != null) {
                    switch (response) {
                        case 0: // Play
                            player.sendMessage("Starting game...");
                            break;
                        case 1: // Settings
                            openSettingsForm(player);
                            break;
                        case 2: // Quit
                            player.kickPlayer("Thanks for playing!");
                            break;
                    }
                }
            }
        );
    }
}
```

## Form Types

Easy4Form supports three main types of forms, each optimized for different use cases:

### 1. Simple Forms
Perfect for menus, navigation, and single-choice selections.

**📖 [Complete Simple Forms Guide →](simple-forms.md)**

```java
SimpleFormBuilder form = new SimpleFormBuilder()
    .title("Main Menu")
    .content("Choose an option:")
    .addButton("Play Game", "textures/ui/play_button")
    .addButton("Settings", "textures/ui/settings_button")
    .addButton("Exit", "textures/ui/exit_button");

Easy4FormAPI.sendSimpleForm(player, form, (response, buttonIndex) -> {
    switch (buttonIndex) {
        case 0: startGame(player); break;
        case 1: showSettings(player); break;
        case 2: player.kickPlayer("Thanks for playing!"); break;
    }
});
```

### 2. Modal Forms
Ideal for yes/no confirmations and binary choices.

**📖 [Complete Modal Forms Guide →](modal-forms.md)**

```java
ModalFormBuilder form = new ModalFormBuilder()
    .title("Confirm Action")
    .content("Are you sure you want to delete your world?")
    .positiveButton("Yes, Delete")
    .negativeButton("Cancel");

Easy4FormAPI.sendModalForm(player, form, confirmed -> {
    if (confirmed) {
        deleteWorld(player);
        player.sendMessage("World deleted successfully!");
    } else {
        player.sendMessage("Action cancelled.");
    }
});
```

### 3. Custom Forms
Powerful forms for complex data collection with multiple input types.

**📖 [Complete Custom Forms Guide →](custom-forms.md)**

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("Player Settings")
    .addInput("username", "Display Name:", "Enter your name")
    .addToggle("notifications", "Enable Notifications", true)
    .addDropdown("difficulty", "Difficulty:", 
                Arrays.asList("Easy", "Normal", "Hard"), 1)
    .addSlider("volume", "Volume:", 0, 100, 5, 75);

Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response != null) {
        String username = response.getInput("username");
        boolean notifications = response.getToggle("notifications");
        String difficulty = response.getDropdown("difficulty");
        double volume = response.getSlider("volume");
        
        // Apply settings
        applyPlayerSettings(player, username, notifications, difficulty, volume);
    }
});
```

## Player Utilities

The proxy API includes helpful utilities for player management:

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// Check if player is a Bedrock player
if (Easy4FormAPI.isBedrockPlayer(player)) {
    // Send form to Bedrock player
    Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
} else {
    // Handle Java player differently
    player.sendMessage("This feature is only available for Bedrock players.");
}

// Get Floodgate player instance
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
if (floodgatePlayer != null) {
    // Access Floodgate-specific features
    String deviceName = floodgatePlayer.getDeviceOs().toString();
    player.sendMessage("Playing on: " + deviceName);
}
```

## Advanced Features

### Error Handling

The proxy API provides comprehensive error handling:

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "Title",
    "Content",
    buttons,
    response -> {
        // Handle successful response
        if (response != null) {
            handleButtonClick(response);
        } else {
            // Form was closed or cancelled
            player.sendMessage("Form cancelled.");
        }
    },
    error -> {
        // Handle errors (optional error handler)
        getLogger().warning("Form error: " + error.getMessage());
        player.sendMessage("An error occurred while displaying the form.");
    }
);
```

### Configuration

Customize the proxy behavior through `config.yml` configuration file:

```yaml
# Easy4Form Configuration
# Easy4Form 配置文件

# Detection mode for Bedrock players
# 基岩版玩家检测模式
# Options: floodgate, uuid
detection-mode: floodgate

# UUID prefix for Bedrock players (only used when detection-mode is 'uuid')
# 基岩版玩家的UUID前缀（仅在detection-mode为'uuid'时使用）
uuid-prefix: "00000000-0000"

# Debug mode
# 调试模式
debug: false

# API version (v1 and v2 are supported, v2 is recommended)
# API版本（支持v1和v2，推荐使用v2）
api-version: "v2"

# Enable migration warnings for deprecated API usage
# 启用已弃用API使用的迁移警告
migration-warnings: true
```

**Note**: 
- Configuration options are determined by the `config.yml` file, please adjust according to your needs
- It's recommended to update configurations with version updates for better handling and implementation
- Some code examples in the documentation may involve pseudo-code or unimplemented specific features that need to be implemented by yourself

## Version Differences

### v1 vs v2 Response Handling Differences

**Important**: v1 and v2 have different response handling when forms are closed:

- **v1 API**: When a form is closed, SimpleForm response handler receives `null` value
- **v2 API**: When a form is closed, SimpleForm response handler receives `-1` value

```java
// v1 Response Handling
response -> {
    if (response == null) {
        // Form was closed
        player.sendMessage("Form cancelled");
    } else {
        // Handle button click
        handleButtonClick(player, response);
    }
}

// v2 Response Handling
.validResultHandler(response -> {
    // Only handle valid button clicks
    handleButtonClick(player, response);
})
.closedOrInvalidResultHandler(() -> {
    // Handle form closure
    player.sendMessage("Form cancelled");
})
```

### API Version Recommendations

- **Recommended**: Main proxy API (automatically selects best version)
- **v2**: Based on new Cumulus interface, better performance and features
- **v1**: Deprecated, for compatibility only

## Migration Guide

### From v1 API

```java
// Old v1 code
import cn.enderrealm.easy4form.api.v1.Easy4FormAPI;

// New proxy code (recommended)
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// Method calls remain the same!
Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
```

### From v2 API

```java
// Old v2 code
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

// New proxy code (recommended)
import cn.enderrealm.easy4form.api.Easy4FormAPI;

// Method calls remain the same!
Easy4FormAPI.sendSimpleForm(player, title, content, buttons, handler);
```

### Benefits of Migration

1. **Future Compatibility**: Your code will work with future Easy4Form versions
2. **Automatic Optimization**: Always uses the best available implementation
3. **Simplified Maintenance**: No need to track version compatibility
4. **Enhanced Features**: Access to proxy-specific features like error handling

## Best Practices

### 1. Always Check for Bedrock Players

```java
if (Easy4FormAPI.isBedrockPlayer(player)) {
    // Send form
} else {
    // Provide alternative for Java players
}
```

### 2. Handle Null Responses

```java
response -> {
    if (response != null) {
        // Process response
    } else {
        // Form was cancelled
    }
}
```

### 3. Use Descriptive Titles and Content

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "Server Menu",  // Clear, descriptive title
    "Choose what you'd like to do:",  // Helpful content
    buttons,
    handler
);
```

### 4. Provide Feedback

```java
response -> {
    if (response != null) {
        player.sendMessage("Processing your selection...");
        // Handle response
    }
}
```

## Troubleshooting

### Common Issues

**Form not displaying:**
- Ensure player is a Bedrock player
- Check if Floodgate is properly installed
- Verify Easy4Form is enabled

**Null responses:**
- Player closed the form
- Network issues
- Form timeout

**Import errors:**
- Use `cn.enderrealm.easy4form.api.Easy4FormAPI`
- Ensure Easy4Form is in your dependencies

### Debug Mode

Enable debug logging for troubleshooting:

```java
Easy4FormAPI.setDebugMode(true);
```

## Support

For help and support:

1. Check the [detailed form guides](simple-forms.md)
2. Review the [troubleshooting section](#troubleshooting)
3. Enable debug mode for detailed logging
4. Check the code repository

---

## Quick Reference

- **[Simple Forms](simple-forms.md)** - Perfect for menus and selections
- **[Modal Forms](modal-forms.md)** - Great for confirmations
- **[Custom Forms](custom-forms.md)** - Advanced input collection

---

**Note**: The example code in this documentation may contain simplified implementations and pseudo-code. Actual implementation details may vary and should be adapted to your specific needs and requirements.

**Ready to get started?** Choose your form type above for detailed guides.