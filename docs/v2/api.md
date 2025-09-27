# Easy4Form API Documentation v2

> **⚠️ NOT RECOMMENDED / 不推荐使用**
> 
> **English**: While this API v2 is based on the new Cumulus interface and is actively maintained, it is **not recommended** for direct use. Please consider:
> - **Recommended**: Use the main proxy package which automatically handles compatibility and provides the best experience
> - **Alternative**: Use this v2 package directly only if you specifically need the new interface features
> 
> **中文**: 虽然此 API v2 基于新版 Cumulus 接口且正在积极维护，但**不推荐**直接使用。请考虑：
> - **推荐**: 使用主代理包，它会自动处理兼容性并提供最佳体验
> - **备选**: 仅在特别需要新接口功能时才直接使用此 v2 包

**Language**: [English](api.md) | [中文](api_zh.md)

**Back to**: [Main Page](../../README.md) | [主页](../../README_ZH.md)

Easy4Form v2 is a modernized Form API for the new Cumulus interface, providing enhanced functionality and better performance for creating forms for Bedrock players.

## Table of Contents

- [Introduction](#introduction)
- [Player Utilities](#player-utilities)
- [Simple Forms](#simple-forms)
- [Modal Forms](#modal-forms)
- [Custom Forms](#custom-forms)
- [Migration from v1](#migration-from-v1)

## Introduction

Easy4Form v2 provides an updated interface for creating and sending forms to Bedrock players through the new Cumulus API. It supports all three types of forms available in Bedrock Edition with improved performance and new features:

- **Simple Forms**: A list of buttons that the player can click
- **Modal Forms**: A dialog with two buttons (yes/no, confirm/cancel, etc.)
- **Custom Forms**: A form with various input elements (text fields, toggles, sliders, dropdowns, etc.)

### Key Improvements in v2

- **New Cumulus Interface**: Built on the latest Cumulus API for better performance
- **Enhanced Response Handling**: More flexible response handling with separate handlers for different scenarios
- **Improved Error Handling**: Better error management and debugging capabilities
- **Modern Java Features**: Utilizes modern Java features and lambda expressions
- **Better Type Safety**: Enhanced type safety and null handling

## Player Utilities

### Check if a player is a Bedrock player

**Method Signature:**
```java
public static boolean isBedrockPlayer(Player player)
```

**Parameters:**
- `player` (Player) - The Bukkit player to check

**Returns:**
- `boolean` - `true` if the player is a Bedrock player, `false` otherwise

**Usage:**
```java
boolean isBedrockPlayer = Easy4FormAPI.isBedrockPlayer(player);
```

**Example:**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

public void onPlayerJoin(Player player) {
    if (Easy4FormAPI.isBedrockPlayer(player)) {
        player.sendMessage("Welcome, Bedrock player!");
    } else {
        player.sendMessage("Welcome, Java player!");
    }
}
```

### Get the FloodgatePlayer instance

**Method Signature:**
```java
public static FloodgatePlayer getFloodgatePlayer(Player player)
```

**Parameters:**
- `player` (Player) - The Bukkit player to get FloodgatePlayer for

**Returns:**
- `FloodgatePlayer` - The FloodgatePlayer instance, or `null` if the player is not a Bedrock player

**Usage:**
```java
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
```

**Example:**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public void getPlayerInfo(Player player) {
    FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
    if (floodgatePlayer != null) {
        String deviceOs = floodgatePlayer.getDeviceOs().toString();
        player.sendMessage("You are playing on: " + deviceOs);
    }
}
```

## Simple Forms

Simple forms consist of a title, content, and a list of buttons.

### Using the Easy4FormAPI

**Method Signature:**
```java
public static void sendSimpleForm(Player player, String title, String content, List<String> buttons, Consumer<Integer> responseHandler)
```

**Parameters:**
- `player` (Player) - The Bukkit player to send the form to
- `title` (String) - The title of the form
- `content` (String) - The content/description text of the form
- `buttons` (List<String>) - List of button texts
- `responseHandler` (Consumer<Integer>) - Callback function that receives the button index (0-based) or `null` if form was closed

**Usage:**
```java
List<String> buttons = Arrays.asList("Button 1", "Button 2", "Button 3");
Easy4FormAPI.sendSimpleForm(player, "Title", "Content", buttons, response -> {
    if (response != null) {
        player.sendMessage("You clicked button: " + buttons.get(response));
    } else {
        player.sendMessage("You closed the form");
    }
});
```

**Example: Server Selector**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

import java.util.Arrays;
import java.util.List;

public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList("Survival", "Creative", "Minigames", "Skyblock");

    Easy4FormAPI.sendSimpleForm(
            player,
            "Server Selector",
            "Choose a server to connect to:",
            servers,
            response -> {
                if (response != null) {
                    String server = servers.get(response);
                    player.sendMessage("Connecting to " + server + "...");
                    // Code to connect player to the selected server
                }
            }
    );
}
```

### Using the SimpleFormBuilder

**Available Methods:**
- `title(String title)` - Set the form title
- `content(String content)` - Set the form content/description
- `button(String text)` - Add a button without an image
- `button(String text, String imagePath)` - Add a button with a resource pack image
- `buttonWithUrl(String text, String imageUrl)` - Add a button with a URL image
- `validResultHandler(Consumer<Integer> handler)` - Set the handler for valid responses
- `closedOrInvalidResultHandler(Runnable handler)` - Set the handler for closed or invalid forms
- `send(Player player)` - Send the form to the player

**Method Parameters:**
- `title` (String) - The title text displayed at the top of the form
- `content` (String) - The description text displayed below the title
- `text` (String) - The button text
- `imagePath` (String) - Resource pack path (e.g., "textures/items/diamond")
- `imageUrl` (String) - Web URL to an image (e.g., "https://example.com/image.png")
- `handler` (Consumer<Integer>) - Callback that receives button index (0-based)
- `handler` (Runnable) - Callback for closed/invalid forms
- `player` (Player) - The Bukkit player to send the form to

**Usage:**
```java
new SimpleFormBuilder()
    .title("Title")
    .content("Content")
    .button("Button 1")                                     // Button without image
    .button("Button 2", "textures/items/diamond")          // Button with resource pack image
    .buttonWithUrl("Button 3", "https://example.com/image.png") // Button with URL image
    .validResultHandler(response -> {
        player.sendMessage("You clicked button: " + response);
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("Form was closed or invalid");
    })
    .send(player);
```

**Example: Shop Categories**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.SimpleFormBuilder;

public void showShopCategories(Player player) {
    new SimpleFormBuilder()
            .title("Shop")
            .content("Select a category:")
            .button("Weapons")
            .button("Armor")
            .button("Tools")
            .button("Food")
            .button("Miscellaneous")
            .validResultHandler(response -> {
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
            .closedOrInvalidResultHandler(() -> {
                // Handle form closure
            })
            .send(player);
}
```

## Modal Forms

Modal forms consist of a title, content, and two buttons (typically for yes/no or confirm/cancel scenarios).

### Using the Easy4FormAPI

**Method Signature:**
```java
public static void sendModalForm(Player player, String title, String content, String button1, String button2, Consumer<Boolean> responseHandler)
```

**Parameters:**
- `player` (Player) - The Bukkit player to send the form to
- `title` (String) - The title of the form
- `content` (String) - The content/description text of the form
- `button1` (String) - Text for the first button (typically "Yes", "Confirm", etc.)
- `button2` (String) - Text for the second button (typically "No", "Cancel", etc.)
- `responseHandler` (Consumer<Boolean>) - Callback function that receives `true` for button1, `false` for button2, or `null` if form was closed

**Usage:**
```java
Easy4FormAPI.sendModalForm(
    player,
    "Confirmation",
    "Are you sure you want to proceed?",
    "Yes",
    "No",
    response -> {
        if (response != null) {
            if (response) {
                player.sendMessage("You confirmed!");
            } else {
                player.sendMessage("You cancelled!");
            }
        }
    }
);
```

**Example: Teleport Confirmation**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.Easy4FormAPI;

public void confirmTeleport(Player player, Location destination) {
    Easy4FormAPI.sendModalForm(
            player,
            "Teleport Confirmation",
            "Do you want to teleport to the specified location?",
            "Teleport",
            "Cancel",
            response -> {
                if (response != null && response) {
                    player.teleport(destination);
                    player.sendMessage("Teleported!");
                }
            }
    );
}
```

### Using the ModalFormBuilder

**Available Methods:**
- `title(String title)` - Set the form title
- `content(String content)` - Set the form content/description
- `button1(String text)` - Set the text for the first button
- `button2(String text)` - Set the text for the second button
- `validResultHandler(Consumer<Boolean> handler)` - Set the handler for valid responses
- `closedOrInvalidResultHandler(Runnable handler)` - Set the handler for closed or invalid forms
- `send(Player player)` - Send the form to the player

**Method Parameters:**
- `title` (String) - The title text displayed at the top of the form
- `content` (String) - The description text displayed below the title
- `text` (String) - The button text
- `handler` (Consumer<Boolean>) - Callback that receives `true` for button1, `false` for button2
- `handler` (Runnable) - Callback for closed/invalid forms
- `player` (Player) - The Bukkit player to send the form to

**Usage:**
```java
new ModalFormBuilder()
    .title("Confirmation")
    .content("Are you sure you want to proceed?")
    .button1("Yes")
    .button2("No")
    .validResultHandler(response -> {
        if (response) {
            player.sendMessage("You confirmed!");
        } else {
            player.sendMessage("You cancelled!");
        }
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("Form was closed");
    })
    .send(player);
```

**Example: Delete Home Confirmation**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.ModalFormBuilder;

public void confirmDeleteHome(Player player, String homeName) {
    new ModalFormBuilder()
            .title("Delete Home")
            .content("Are you sure you want to delete your home \"" + homeName + "\"?")
            .button1("Delete")
            .button2("Cancel")
            .validResultHandler(response -> {
                if (response) {
                    // Code to delete the home
                    player.sendMessage("Home \"" + homeName + "\" has been deleted.");
                }
            })
            .closedOrInvalidResultHandler(() -> {
                // Handle form closure
            })
            .send(player);
}
```

## Custom Forms

Custom forms allow you to create forms with various input elements such as text fields, toggles, sliders, dropdowns, etc.

### Using the CustomFormBuilder

**Available Methods:**
- `title(String title)` - Set the form title
- `label(String id, String text)` - Add a text label (display only)
- `input(String id, String label, String placeholder, String defaultValue)` - Add a text input field
- `toggle(String id, String label, boolean defaultValue)` - Add a toggle switch (boolean)
- `slider(String id, String label, float min, float max, float step, float defaultValue)` - Add a slider
- `dropdown(String id, String label, List<String> options, int defaultIndex)` - Add a dropdown menu
- `stepSlider(String id, String label, List<String> steps, int defaultIndex)` - Add a step slider
- `validResultHandler(Consumer<Map<String, Object>> handler)` - Set the handler for valid responses
- `invalidResultHandler(Runnable handler)` - Set the handler for invalid forms
- `send(Player player)` - Send the form to the player

**Element Parameters:**
- `id` (String) - Unique identifier for the element (used in response map)
- `label` (String) - Display text for the element
- `text` (String) - Text content for labels
- `placeholder` (String) - Placeholder text for input fields
- `defaultValue` (String/boolean/float) - Default value for the element
- `min/max` (float) - Range limits for sliders
- `step` (float) - Step increment for sliders
- `options/steps` (List<String>) - Available options for dropdowns/step sliders
- `defaultIndex` (int) - Default selected index for dropdowns/step sliders

**Usage:**
```java
new CustomFormBuilder()
    .title("Settings")
    .label("info", "Please configure your settings below:")
    .input("name", "Name", "Enter your name", player.getName())
    .toggle("notifications", "Enable Notifications", true)
    .slider("volume", "Volume", 0, 100, 10, 50)
    .dropdown("difficulty", "Difficulty", Arrays.asList("Easy", "Normal", "Hard"), 1)
    .validResultHandler(response -> {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        int volume = ((Float) response.get("volume")).intValue();
        String difficulty = (String) response.get("difficulty");
        
        player.sendMessage("Name: " + name);
        player.sendMessage("Notifications: " + notifications);
        player.sendMessage("Volume: " + volume);
        player.sendMessage("Difficulty: " + difficulty);
    })
    .invalidResultHandler(() -> {
        player.sendMessage("Form was closed or invalid");
    })
    .send(player);
```

**Example: Player Profile Form**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.CustomFormBuilder;

import java.util.Arrays;
import java.util.List;

public void showProfileForm(Player player) {
    List<String> genders = Arrays.asList("Male", "Female", "Other", "Prefer not to say");
    List<String> ageRanges = Arrays.asList("Under 18", "18-24", "25-34", "35-44", "45+", "Prefer not to say");

    new CustomFormBuilder()
            .title("Player Profile")
            .label("header", "Please fill out your profile information:")
            .input("nickname", "Nickname", "Enter your preferred nickname", player.getName())
            .input("bio", "Biography", "Tell us about yourself", "")
            .toggle("showProfile", "Show my profile to other players", true)
            .dropdown("gender", "Gender", genders, 0)
            .dropdown("ageRange", "Age Range", ageRanges, 0)
            .validResultHandler(response -> {
                // Save the profile information
                String nickname = (String) response.get("nickname");
                String bio = (String) response.get("bio");
                boolean showProfile = (boolean) response.get("showProfile");
                String gender = genders.get(((Float) response.get("gender")).intValue());
                String ageRange = ageRanges.get(((Float) response.get("ageRange")).intValue());

                // Code to save the profile information
                player.sendMessage("Profile updated successfully!");
            })
            .invalidResultHandler(() -> {
                // Handle form closure or invalid input
            })
            .send(player);
}
```

**Example: Server Feedback Form**

```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.v2.CustomFormBuilder;

import java.util.Arrays;
import java.util.List;

public void showFeedbackForm(Player player) {
    List<String> ratings = Arrays.asList("Poor", "Fair", "Good", "Very Good", "Excellent");
    List<String> aspects = Arrays.asList("Gameplay", "Community", "Staff", "Events", "Performance");

    new CustomFormBuilder()
            .title("Server Feedback")
            .label("intro", "We value your feedback! Please let us know how we're doing.")
            .dropdown("overall", "Overall Experience", ratings, 2)
            .stepSlider("favorite", "Favorite Aspect", aspects, 0)
            .input("suggestions", "Suggestions", "Enter your suggestions here", "")
            .toggle("contact", "May we contact you for more feedback?", false)
            .validResultHandler(response -> {
                String overallRating = ratings.get(((Float) response.get("overall")).intValue());
                String favoriteAspect = aspects.get(((Float) response.get("favorite")).intValue());
                String suggestions = (String) response.get("suggestions");
                boolean canContact = (boolean) response.get("contact");

                // Code to save the feedback
                player.sendMessage("Thank you for your feedback!");

                // Notify admins about new feedback
                for (Player admin : getAdminPlayers()) {
                    admin.sendMessage("New feedback from " + player.getName() + ":");
                    admin.sendMessage("Overall: " + overallRating);
                    admin.sendMessage("Favorite: " + favoriteAspect);
                    admin.sendMessage("Suggestions: " + suggestions);
                }
            })
            .invalidResultHandler(() -> {
                // Handle form closure
            })
            .send(player);
}

private List<Player> getAdminPlayers() {
    // Code to get online admin players
    return new ArrayList<>();
}
```

## Migration from v1

### Key Changes

1. **Response Handling**: v2 uses separate handlers for different response types:
   - `validResultHandler()` - For valid form responses
   - `closedOrInvalidResultHandler()` / `invalidResultHandler()` - For closed or invalid forms

2. **Custom Form Response Access**: v2 uses the new Cumulus iterator pattern:
   - v1: `response.responses().get(index)`
   - v2: `response.valueAt(index)` with `response.reset()`

3. **Lambda Expressions**: v2 uses more concise lambda expressions for better performance

4. **Package Structure**: Import from `cn.enderrealm.easy4form.api.v2` instead of `v1`

### Migration Example

**v1 Code:**
```java
new SimpleFormBuilder()
    .title("Test")
    .button("Button 1")
    .responseHandler(response -> {
        if (response != null) {
            player.sendMessage("Clicked: " + response);
        } else {
            player.sendMessage("Closed");
        }
    })
    .send(player);
```

**v2 Code:**
```java
new SimpleFormBuilder()
    .title("Test")
    .button("Button 1")
    .validResultHandler(response -> {
        player.sendMessage("Clicked: " + response);
    })
    .closedOrInvalidResultHandler(() -> {
        player.sendMessage("Closed");
    })
    .send(player);
```

## Additional Notes

- All form builders use a fluent interface, allowing method chaining for cleaner code.
- Response handlers are called asynchronously, so be careful when interacting with the Bukkit API.
- v2 provides better error handling and debugging capabilities compared to v1.
- The new response handling system allows for more precise control over different form states.
- Lambda expressions are optimized for better performance in v2.