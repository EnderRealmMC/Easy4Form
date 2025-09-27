# Simple Forms - Easy4Form Main Proxy API

**Language**: [English](simple-forms.md) | [中文](simple-forms_zh.md)

**Back to**: [Main API Documentation](README.md)

Simple Forms are button-based forms that present users with a list of clickable options. They're perfect for menus, navigation, and any scenario where users need to choose from multiple options.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Advanced Features](#advanced-features)
- [Builder Pattern](#builder-pattern)
- [Best Practices](#best-practices)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Overview

### What are Simple Forms?

Simple Forms display:
- A **title** at the top
- **content text** describing the purpose
- A list of **buttons** that users can click
- Each button returns its **index** (0, 1, 2, etc.) when clicked

### When to Use Simple Forms

✅ **Perfect for:**
- Main menus
- Category selection
- Navigation between features
- Lists of actions or commands
- Server selection

❌ **Not ideal for:**
- Yes/No questions (use [Modal Forms](modal-forms.md))
- Data input (use [Custom Forms](custom-forms.md))
- Forms with more than 10-12 buttons (UX considerations)

## Basic Usage

### Simple Example

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import java.util.Arrays;

public void showMainMenu(Player player) {
    List<String> buttons = Arrays.asList(
        "Play Games",
        "View Stats",
        "Settings",
        "Exit"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "Main Menu",                    // Title
        "Welcome! Choose an option:",   // Content
        buttons,                        // Button list
        response -> {                   // Response handler
            if (response != null) {
                handleMenuSelection(player, response);
            } else {
                player.sendMessage("Menu cancelled.");
            }
        }
    );
}

private void handleMenuSelection(Player player, int buttonIndex) {
    switch (buttonIndex) {
        case 0: // Play Games
            showGameMenu(player);
            break;
        case 1: // View Stats
            showPlayerStats(player);
            break;
        case 2: // Settings
            showSettingsMenu(player);
            break;
        case 3: // Exit
            player.sendMessage("Thanks for playing!");
            break;
        default:
            player.sendMessage("Invalid selection.");
    }
}
```

### Response Handling

The response handler receives an `Integer` that can be:
- **0, 1, 2, etc.**: The index of the clicked button
- **null**: The form was closed/cancelled

```java
response -> {
    if (response == null) {
        // Form was closed or cancelled
        player.sendMessage("Form cancelled.");
        return;
    }
    
    // Handle button click
    switch (response) {
        case 0:
            // First button clicked
            break;
        case 1:
            // Second button clicked
            break;
        // ... more cases
    }
}
```

## Advanced Features

### Dynamic Button Generation

```java
public void showPlayerList(Player player) {
    List<String> buttons = new ArrayList<>();
    
    // Add online players as buttons
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        if (!onlinePlayer.equals(player)) {
            buttons.add(onlinePlayer.getName());
        }
    }
    
    // Add navigation buttons
    buttons.add("Refresh");
    buttons.add("Cancel");
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "Online Players",
        "Select a player to interact with:",
        buttons,
        response -> {
            if (response == null) return;
            
            if (response == buttons.size() - 2) {
                // Refresh button
                showPlayerList(player); // Recursive call
            } else if (response == buttons.size() - 1) {
                // Cancel button
                player.sendMessage("Cancelled.");
            } else {
                // Player selected
                String selectedPlayerName = buttons.get(response);
                Player selectedPlayer = Bukkit.getPlayer(selectedPlayerName);
                if (selectedPlayer != null) {
                    showPlayerInteractionMenu(player, selectedPlayer);
                }
            }
        }
    );
}
```

### Error Handling

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "Title",
    "Content",
    buttons,
    response -> {
        try {
            if (response != null) {
                handleResponse(player, response);
            }
        } catch (Exception e) {
            player.sendMessage("An error occurred while processing your selection.");
            getLogger().severe("Error handling form response: " + e.getMessage());
        }
    }
);
```

## Builder Pattern

For more complex forms, use the builder pattern:

```java
import cn.enderrealm.easy4form.api.SimpleFormBuilder;

public void showAdvancedMenu(Player player) {
    new SimpleFormBuilder()
        .title("Advanced Menu")
        .content("Choose your action carefully:")
        .button("Mini Games")
        .button("Leaderboards")
        .button("Shop")
        .button("Settings")
        .button("Exit")
        .responseHandler(response -> {
            if (response != null) {
                handleAdvancedMenuResponse(player, response);
            }
        })
        .send(player);
}
```

### Builder Methods

```java
SimpleFormBuilder builder = new SimpleFormBuilder()
    .title("Form Title")                    // Set form title
    .content("Form description")            // Set form content
    .button("Button 1")                     // Add a button
    .button("Button 2")                     // Add another button
    .buttons(Arrays.asList("A", "B", "C"))  // Add multiple buttons
    .responseHandler(response -> { ... })   // Set response handler
    .errorHandler(error -> { ... });       // Set error handler (optional)

// Send the form
builder.send(player);
```

## Best Practices

### 1. Keep Button Text Concise

```java
// Good
List<String> buttons = Arrays.asList(
    "Play",
    "Stats",
    "Settings",
    "Quit"
);

// Avoid
List<String> buttons = Arrays.asList(
    "Click here to start playing the game",
    "View your detailed statistics and achievements",
    "Open the settings and configuration menu",
    "Exit the game and return to the server lobby"
);
```

### 2. Provide Clear Navigation

```java
// Always include navigation options
List<String> buttons = Arrays.asList(
    "Option 1",
    "Option 2",
    "Option 3",
    "Back",      // Return to previous menu
    "Cancel"     // Close form
);
```

### 4. Handle Edge Cases

```java
response -> {
    if (response == null) {
        // Form cancelled
        return;
    }
    
    if (response < 0 || response >= buttons.size()) {
        // Invalid response (shouldn't happen, but be safe)
        player.sendMessage("Invalid selection.");
        return;
    }
    
    // Process valid response
    handleValidResponse(player, response);
}
```

### 5. Limit Button Count

```java
// Good - reasonable number of options
List<String> buttons = Arrays.asList(
    "Option 1", "Option 2", "Option 3", "Option 4", "Option 5"
);

// Consider pagination for many options
if (allOptions.size() > 8) {
    showPaginatedMenu(player, allOptions, 0);
} else {
    showSimpleMenu(player, allOptions);
}
```

## Examples

### Example 1: Server Selector

```java
public void showServerSelector(Player player) {
    List<String> servers = Arrays.asList(
        "Survival",
        "PvP Arena",
        "Mini Games",
        "Creative",
        "Back to Lobby"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "Server Selection",
        "Choose a server to join:",
        servers,
        response -> {
            if (response == null) return;
            
            switch (response) {
                case 0: // Survival
                    connectToServer(player, "survival");
                    break;
                case 1: // PvP
                    connectToServer(player, "pvp");
                    break;
                case 2: // Mini Games
                    showMiniGameSelector(player);
                    break;
                case 3: // Creative
                    connectToServer(player, "creative");
                    break;
                case 4: // Back
                    showMainMenu(player);
                    break;
            }
        }
    );
}
```

### Example 2: Shop Categories

```java
public void showShopCategories(Player player) {
    List<String> categories = Arrays.asList(
        "Weapons",
        "Armor",
        "Food",
        "Tools",
        "Special Items",
        "View Balance",
        "Exit Shop"
    );
    
    Easy4FormAPI.sendSimpleForm(
        player,
        "Shop",
        String.format("Welcome %s! Your balance: $%.2f", 
                     player.getName(), getPlayerBalance(player)),
        categories,
        response -> {
            if (response == null) return;
            
            if (response == categories.size() - 2) {
                // View Balance
                player.sendMessage(String.format("Your balance: $%.2f", 
                                                getPlayerBalance(player)));
                showShopCategories(player); // Show menu again
            } else if (response == categories.size() - 1) {
                // Exit
                player.sendMessage("Thanks for visiting the shop!");
            } else {
                // Show category items
                showCategoryItems(player, response);
            }
        }
    );
}
```

### Example 3: Paginated List

```java
public void showPaginatedList(Player player, List<String> allItems, int page) {
    int itemsPerPage = 6;
    int startIndex = page * itemsPerPage;
    int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());
    
    List<String> buttons = new ArrayList<>();
    
    // Add items for current page
    for (int i = startIndex; i < endIndex; i++) {
        buttons.add(allItems.get(i));
    }
    
    // Add navigation buttons
    if (page > 0) {
        buttons.add("Previous Page");
    }
    if (endIndex < allItems.size()) {
        buttons.add("Next Page");
    }
    buttons.add("Close");
    
    String title = String.format("Items (Page %d/%d)", 
                                page + 1, 
                                (allItems.size() + itemsPerPage - 1) / itemsPerPage);
    
    Easy4FormAPI.sendSimpleForm(
        player,
        title,
        "Select an item:",
        buttons,
        response -> {
            if (response == null) return;
            
            int itemCount = endIndex - startIndex;
            
            if (response < itemCount) {
                // Item selected
                String selectedItem = allItems.get(startIndex + response);
                handleItemSelection(player, selectedItem);
            } else {
                // Navigation button
                int navIndex = response - itemCount;
                if (page > 0 && navIndex == 0) {
                    // Previous page
                    showPaginatedList(player, allItems, page - 1);
                } else if (endIndex < allItems.size() && 
                          ((page > 0 && navIndex == 1) || (page == 0 && navIndex == 0))) {
                    // Next page
                    showPaginatedList(player, allItems, page + 1);
                }
                // Close button - do nothing, form closes automatically
            }
        }
    );
}
```

## Troubleshooting

### Common Issues

**Buttons not showing:**
- Ensure the button list is not empty
- Check that all button strings are not null
- Verify the player is a Bedrock player

**Wrong button clicked:**
- Remember that button indices start at 0
- Check your response handling logic
- Use debugging to log the response value

**Form not displaying:**
- Verify Easy4FormAPI.isBedrockPlayer(player) returns true
- Check server logs for errors
- Ensure Floodgate is properly configured

### Debug Example

```java
Easy4FormAPI.sendSimpleForm(
    player,
    "Debug Form",
    "Testing form display",
    Arrays.asList("Button 1", "Button 2", "Button 3"),
    response -> {
        // Log the response for debugging
        getLogger().info(String.format("Player %s clicked button: %s", 
                                      player.getName(), 
                                      response != null ? response.toString() : "null"));
        
        if (response != null) {
            player.sendMessage("You clicked button " + (response + 1));
        } else {
            player.sendMessage("Form was cancelled");
        }
    }
);
```

## Related Documentation

- **[Modal Forms](modal-forms.md)** - For Yes/No confirmations
- **[Custom Forms](custom-forms.md)** - For data input
- **[Main API Documentation](README.md)** - Overview and getting started
- **[Player Utilities](README.md#player-utilities)** - Helper methods

---

**Next Steps:**
- Try the [Modal Forms guide](modal-forms.md) for confirmation dialogs
- Explore [Custom Forms](custom-forms.md) for advanced input collection
- Return to [Main API Documentation](README.md) for other features