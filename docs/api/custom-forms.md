# Custom Forms - Easy4Form Main Proxy API

**Language**: [English](custom-forms.md) | [中文](custom-forms_zh.md)

**Back to**: [Main API Documentation](README.md)

Custom Forms are the most powerful and flexible form type in Easy4Form. They allow you to collect various types of input from users including text, numbers, toggles, dropdowns, and sliders. Perfect for configuration screens, user registration, settings panels, and any scenario requiring structured data input.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Form Elements](#form-elements)
- [Advanced Features](#advanced-features)
- [Builder Pattern](#builder-pattern)
- [Best Practices](#best-practices)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Overview

### What are Custom Forms?

Custom Forms provide:
- **Multiple input types**: Text, numbers, toggles, dropdowns, sliders
- **Structured data collection**: Organized form with labels and descriptions
- **Flexible layout**: Add elements in any order
- **Rich responses**: Returns a list of values corresponding to each form element
- **Validation support**: Built-in and custom validation options

### When to Use Custom Forms

✅ **Perfect for:**
- User registration and profiles
- Configuration and settings panels
- Data entry forms
- Surveys and questionnaires
- Complex input collection
- Multi-step wizards

❌ **Not ideal for:**
- Simple yes/no questions (use [Modal Forms](modal-forms.md))
- Menu navigation (use [Simple Forms](simple-forms.md))
- Single-choice selections (use [Simple Forms](simple-forms.md))

## Basic Usage

### Simple Example

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import cn.enderrealm.easy4form.api.CustomFormBuilder;

public void showPlayerRegistration(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Player Registration")
        .addInput("username", "Username:", "Enter your display name")
        .addInput("email", "Email:", "your@email.com")
        .addToggle("newsletter", "Subscribe to newsletter", false)
        .addDropdown("region", "Select Region:", 
                    Arrays.asList("North America", "Europe", "Asia", "Other"), 0);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            String username = response.getInput("username");
            String email = response.getInput("email");
            boolean newsletter = response.getToggle("newsletter");
            String region = response.getDropdown("region");
            
            // Process registration
            registerPlayer(player, username, email, newsletter, region);
            player.sendMessage("Registration completed successfully!");
        } else {
            player.sendMessage("Registration cancelled.");
        }
    });
}
```

### Response Handling

Custom Form responses provide typed access to form data:

```java
response -> {
    if (response == null) {
        // Form was closed without submission
        player.sendMessage("Form cancelled.");
        return;
    }
    
    // Access form data by element ID
    String textValue = response.getInput("elementId");
    boolean toggleValue = response.getToggle("toggleId");
    String dropdownValue = response.getDropdown("dropdownId");
    double sliderValue = response.getSlider("sliderId");
    
    // Process the collected data
    processFormData(player, textValue, toggleValue, dropdownValue, sliderValue);
}
```

## Form Elements

### 1. Text Input

Collects text input from users:

```java
// Basic text input
form.addInput("name", "Player Name:", "Enter your name");

// With validation
form.addInput("username", "Username:", "3-16 characters", input -> {
    if (input.length() < 3 || input.length() > 16) {
        return "Username must be 3-16 characters long";
    }
    if (!input.matches("[a-zA-Z0-9_]+")) {
        return "Username can only contain letters, numbers, and underscores";
    }
    return null; // Valid
});

// Multiline text input
form.addTextArea("description", "Description:", "Enter a detailed description...");
```

### 2. Toggle (Boolean)

Provides on/off switches:

```java
// Basic toggle
form.addToggle("pvp", "Enable PvP", false); // Default: false

// With description
form.addToggle("notifications", "Receive Notifications", true, 
              "Get notified about important server events");
```

### 3. Dropdown (Selection)

Offers multiple choice selection:

```java
// Basic dropdown
List<String> options = Arrays.asList("Easy", "Normal", "Hard", "Expert");
form.addDropdown("difficulty", "Difficulty Level:", options, 1); // Default: "Normal"

// Dynamic dropdown
List<String> availableWorlds = getAvailableWorlds();
form.addDropdown("world", "Select World:", availableWorlds, 0);

// With custom display values
Map<String, String> regionMap = new HashMap<>();
regionMap.put("na", "North America");
regionMap.put("eu", "Europe");
regionMap.put("asia", "Asia Pacific");
form.addDropdown("region", "Region:", regionMap, "na");
```

### 4. Slider (Numeric Range)

Allows numeric input within a range:

```java
// Basic slider
form.addSlider("volume", "Volume Level:", 0.0, 100.0, 1.0, 50.0); // Min, Max, Step, Default

// With custom formatting
form.addSlider("speed", "Movement Speed:", 0.1, 2.0, 0.1, 1.0, 
              value -> String.format("%.1fx", value));

// Integer slider
form.addIntSlider("players", "Max Players:", 1, 20, 1, 10);
```

### 5. Number Input

Direct numeric input:

```java
// Basic number input
form.addNumber("amount", "Amount:", "Enter amount", 0.0);

// With range validation
form.addNumber("price", "Price:", "$0.00", 0.0, number -> {
    if (number < 0) {
        return "Price cannot be negative";
    }
    if (number > 10000) {
        return "Price cannot exceed $10,000";
    }
    return null; // Valid
});

// Integer input
form.addInteger("quantity", "Quantity:", "Enter quantity", 1);
```

## Advanced Features

### Conditional Elements

Show/hide elements based on other inputs:

```java
public void showConditionalForm(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Server Settings")
        .addToggle("enablePvP", "Enable PvP", false)
        .addConditionalGroup("pvpSettings", "enablePvP", true, group -> {
            group.addSlider("pvpDamage", "PvP Damage Multiplier:", 0.1, 3.0, 0.1, 1.0);
            group.addToggle("friendlyFire", "Friendly Fire", false);
            group.addDropdown("pvpZones", "PvP Zones:", 
                            Arrays.asList("Everywhere", "Designated Areas", "Arenas Only"), 1);
        })
        .addToggle("enableEconomy", "Enable Economy", true)
        .addConditionalGroup("economySettings", "enableEconomy", true, group -> {
            group.addNumber("startingMoney", "Starting Money:", "$100.00", 100.0);
            group.addSlider("taxRate", "Tax Rate (%):", 0.0, 25.0, 0.5, 5.0);
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            processServerSettings(player, response);
        }
    });
}
```

### Form Validation

Validate entire form before submission:

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("User Account")
    .addInput("username", "Username:", "Enter username")
    .addInput("password", "Password:", "Enter password")
    .addInput("confirmPassword", "Confirm Password:", "Confirm password")
    .addFormValidator(response -> {
        String password = response.getInput("password");
        String confirmPassword = response.getInput("confirmPassword");
        
        if (!password.equals(confirmPassword)) {
            return "Passwords do not match";
        }
        
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        return null; // Valid
    });
```

### Multi-Page Forms

Create wizard-style multi-step forms:

```java
public void showRegistrationWizard(Player player) {
    showRegistrationStep1(player, new HashMap<>());
}

private void showRegistrationStep1(Player player, Map<String, Object> data) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Registration - Step 1 of 3")
        .addLabel("Personal Information")
        .addInput("firstName", "First Name:", "Enter first name")
        .addInput("lastName", "Last Name:", "Enter last name")
        .addInput("email", "Email:", "your@email.com");
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            data.put("firstName", response.getInput("firstName"));
            data.put("lastName", response.getInput("lastName"));
            data.put("email", response.getInput("email"));
            
            showRegistrationStep2(player, data);
        } else {
            player.sendMessage("Registration cancelled.");
        }
    });
}

private void showRegistrationStep2(Player player, Map<String, Object> data) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Registration - Step 2 of 3")
        .addLabel("Preferences")
        .addDropdown("region", "Region:", 
                    Arrays.asList("North America", "Europe", "Asia", "Other"), 0)
        .addToggle("newsletter", "Subscribe to newsletter", false)
        .addSlider("experience", "Gaming Experience (years):", 0, 20, 1, 5);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            data.put("region", response.getDropdown("region"));
            data.put("newsletter", response.getToggle("newsletter"));
            data.put("experience", response.getSlider("experience"));
            
            showRegistrationStep3(player, data);
        } else {
            // Go back to step 1
            showRegistrationStep1(player, data);
        }
    });
}

private void showRegistrationStep3(Player player, Map<String, Object> data) {
    // Final confirmation step
    String summary = String.format(
        "Please confirm your registration:\n\n" +
        "Name: %s %s\n" +
        "Email: %s\n" +
        "Region: %s\n" +
        "Newsletter: %s\n" +
        "Experience: %.0f years",
        data.get("firstName"), data.get("lastName"),
        data.get("email"), data.get("region"),
        data.get("newsletter"), data.get("experience")
    );
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Registration - Step 3 of 3")
        .addLabel(summary)
        .addToggle("confirm", "I confirm this information is correct", false);
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null && response.getToggle("confirm")) {
            // Complete registration
            completeRegistration(player, data);
            player.sendMessage("Registration completed successfully!");
        } else if (response != null) {
            player.sendMessage("Please confirm your information to complete registration.");
            showRegistrationStep3(player, data);
        } else {
            // Go back to step 2
            showRegistrationStep2(player, data);
        }
    });
}
```

## Builder Pattern

The CustomFormBuilder provides a fluent API for form creation:

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("Form Title")                                    // Set form title
    .addLabel("Section Header")                             // Add descriptive text
    .addInput("id", "Label:", "Placeholder")                // Add text input
    .addToggle("id", "Label", defaultValue)                 // Add toggle
    .addDropdown("id", "Label:", options, defaultIndex)     // Add dropdown
    .addSlider("id", "Label:", min, max, step, defaultValue) // Add slider
    .addNumber("id", "Label:", "Placeholder", defaultValue)  // Add number input
    .addSeparator()                                         // Add visual separator
    .setResponseHandler(response -> { ... })                // Set response handler
    .setErrorHandler(error -> { ... });                    // Set error handler

// Send the form
Easy4FormAPI.sendCustomForm(player, form, responseHandler);
```

## Best Practices

### 1. Organize with Labels and Separators

```java
// Good - Well organized
CustomFormBuilder form = new CustomFormBuilder()
    .title("Player Settings")
    .addLabel("Game Preferences")
    .addDropdown("difficulty", "Difficulty:", difficulties, 1)
    .addToggle("autoSave", "Auto-save", true)
    .addSeparator()
    .addLabel("Audio Settings")
    .addSlider("volume", "Master Volume:", 0, 100, 5, 75)
    .addToggle("soundEffects", "Sound Effects", true)
    .addSeparator()
    .addLabel("Notifications")
    .addToggle("chatNotifications", "Chat Notifications", true);

// Avoid - No organization
CustomFormBuilder form = new CustomFormBuilder()
    .title("Settings")
    .addDropdown("difficulty", "Difficulty:", difficulties, 1)
    .addSlider("volume", "Volume:", 0, 100, 5, 75)
    .addToggle("autoSave", "Auto-save", true)
    .addToggle("soundEffects", "Sound Effects", true);
```

### 2. Use Descriptive Labels and Placeholders

```java
// Good - Clear and descriptive
form.addInput("serverName", "Server Name:", "Enter a unique server name (3-32 characters)");
form.addNumber("maxPlayers", "Maximum Players:", "1-100 players", 20.0);
form.addSlider("difficulty", "Difficulty Multiplier:", 0.5, 3.0, 0.1, 1.0, 
              value -> String.format("%.1fx (%.0f%% damage)", value, value * 100));

// Avoid - Vague labels
form.addInput("name", "Name:", "Name");
form.addNumber("number", "Number:", "Number", 0.0);
form.addSlider("slider", "Value:", 0, 100, 1, 50);
```

### 3. Provide Sensible Defaults

```java
// Good - Thoughtful defaults
form.addToggle("enablePvP", "Enable PvP", false);           // Safe default
form.addSlider("spawnRate", "Mob Spawn Rate:", 0.1, 3.0, 0.1, 1.0); // Normal rate
form.addDropdown("gamemode", "Default Gamemode:", 
                Arrays.asList("Survival", "Creative", "Adventure"), 0); // Survival

// Avoid - Poor defaults
form.addToggle("deleteAllData", "Delete All Data", true);    // Dangerous default
form.addSlider("difficulty", "Difficulty:", 0, 10, 1, 10);   // Maximum difficulty
```

### 4. Validate Input Appropriately

```java
// Good - Comprehensive validation
form.addInput("username", "Username:", "3-16 characters, letters/numbers only", input -> {
    if (input.trim().isEmpty()) {
        return "Username cannot be empty";
    }
    if (input.length() < 3 || input.length() > 16) {
        return "Username must be 3-16 characters long";
    }
    if (!input.matches("[a-zA-Z0-9_]+")) {
        return "Username can only contain letters, numbers, and underscores";
    }
    if (isUsernameTaken(input)) {
        return "Username is already taken";
    }
    return null;
});

// Avoid - No validation
form.addInput("username", "Username:", "Enter username"); // No validation
```

### 5. Handle Errors Gracefully

```java
Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response == null) {
        player.sendMessage("Form cancelled.");
        return;
    }
    
    try {
        // Process form data
        processFormData(player, response);
        player.sendMessage("Settings saved successfully!");
    } catch (ValidationException e) {
        player.sendMessage("Invalid input: " + e.getMessage());
        // Optionally re-show form with error message
        showFormWithError(player, e.getMessage());
    } catch (Exception e) {
        player.sendMessage("An error occurred while saving settings. Please try again.");
        getLogger().severe("Form processing error: " + e.getMessage());
    }
});
```

## Examples

### Example 1: Server Configuration Panel

```java
public void showServerConfig(Player admin) {
    if (!admin.hasPermission("server.admin")) {
        admin.sendMessage("You don't have permission to access server configuration.");
        return;
    }
    
    // Get current server settings
    ServerConfig config = getServerConfig();
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Server Configuration")
        
        // Basic Settings
        .addLabel("Basic Settings")
        .addInput("serverName", "Server Name:", "Enter server name", config.getServerName())
        .addInput("motd", "Message of the Day:", "Welcome message", config.getMotd())
        .addIntSlider("maxPlayers", "Max Players:", 1, 100, 1, config.getMaxPlayers())
        .addSeparator()
        
        // Gameplay Settings
        .addLabel("Gameplay Settings")
        .addDropdown("difficulty", "Difficulty:", 
                    Arrays.asList("Peaceful", "Easy", "Normal", "Hard"), 
                    config.getDifficultyIndex())
        .addToggle("pvp", "Enable PvP", config.isPvpEnabled())
        .addToggle("mobSpawning", "Mob Spawning", config.isMobSpawningEnabled())
        .addSlider("mobSpawnRate", "Mob Spawn Rate:", 0.1, 3.0, 0.1, config.getMobSpawnRate(),
                  value -> String.format("%.1fx", value))
        .addSeparator()
        
        // World Settings
        .addLabel("World Settings")
        .addToggle("generateStructures", "Generate Structures", config.isGenerateStructures())
        .addToggle("allowNether", "Allow Nether", config.isAllowNether())
        .addToggle("allowEnd", "Allow End", config.isAllowEnd())
        .addIntSlider("viewDistance", "View Distance:", 3, 32, 1, config.getViewDistance())
        .addSeparator()
        
        // Economy Settings (if economy plugin is enabled)
        .addConditionalGroup("economyGroup", () -> isEconomyEnabled(), group -> {
            group.addLabel("Economy Settings");
            group.addNumber("startingMoney", "Starting Money:", "$0.00", config.getStartingMoney());
            group.addSlider("shopTaxRate", "Shop Tax Rate (%):", 0.0, 25.0, 0.5, config.getShopTaxRate());
            group.addToggle("allowPlayerShops", "Allow Player Shops", config.isAllowPlayerShops());
        })
        
        // Validation
        .addFormValidator(response -> {
            String serverName = response.getInput("serverName");
            if (serverName.trim().isEmpty()) {
                return "Server name cannot be empty";
            }
            if (serverName.length() > 50) {
                return "Server name cannot exceed 50 characters";
            }
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(admin, form, response -> {
        if (response != null) {
            try {
                // Apply configuration changes
                applyServerConfig(response);
                admin.sendMessage("Server configuration updated successfully!");
                
                // Log the change
                getLogger().info(String.format("Server configuration updated by %s", admin.getName()));
                
                // Notify other admins
                notifyAdmins(String.format("%s updated the server configuration", admin.getName()));
                
            } catch (Exception e) {
                admin.sendMessage("Error updating server configuration: " + e.getMessage());
                getLogger().severe("Server config update failed: " + e.getMessage());
            }
        } else {
            admin.sendMessage("Server configuration update cancelled.");
        }
    });
}

private void applyServerConfig(CustomFormResponse response) {
    ServerConfig config = getServerConfig();
    
    config.setServerName(response.getInput("serverName"));
    config.setMotd(response.getInput("motd"));
    config.setMaxPlayers((int) response.getSlider("maxPlayers"));
    config.setDifficulty(response.getDropdownIndex("difficulty"));
    config.setPvpEnabled(response.getToggle("pvp"));
    config.setMobSpawningEnabled(response.getToggle("mobSpawning"));
    config.setMobSpawnRate(response.getSlider("mobSpawnRate"));
    config.setGenerateStructures(response.getToggle("generateStructures"));
    config.setAllowNether(response.getToggle("allowNether"));
    config.setAllowEnd(response.getToggle("allowEnd"));
    config.setViewDistance((int) response.getSlider("viewDistance"));
    
    if (isEconomyEnabled()) {
        config.setStartingMoney(response.getNumber("startingMoney"));
        config.setShopTaxRate(response.getSlider("shopTaxRate"));
        config.setAllowPlayerShops(response.getToggle("allowPlayerShops"));
    }
    
    config.save();
    config.apply();
}
```

### Example 2: Player Profile Creation

```java
public void showProfileCreation(Player player) {
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Create Your Profile")
        
        // Personal Information
        .addLabel("Personal Information")
        .addInput("displayName", "Display Name:", "How others will see you", player.getName())
        .addTextArea("bio", "Bio:", "Tell others about yourself (optional)")
        .addDropdown("timezone", "Timezone:", getTimezones(), getDefaultTimezoneIndex())
        .addSeparator()
        
        // Gaming Preferences
        .addLabel("Gaming Preferences")
        .addDropdown("favoriteGamemode", "Favorite Gamemode:", 
                    Arrays.asList("Survival", "Creative", "Adventure", "Spectator"), 0)
        .addSlider("skillLevel", "Skill Level:", 1, 10, 1, 5,
                  value -> {
                      String[] levels = {"Beginner", "Novice", "Intermediate", 
                                       "Advanced", "Expert", "Master"};
                      int index = Math.min((int)((value - 1) / 2), levels.length - 1);
                      return String.format("%.0f - %s", value, levels[index]);
                  })
        .addMultiSelect("interests", "Interests:", 
                       Arrays.asList("Building", "Redstone", "PvP", "Exploration", 
                                   "Farming", "Trading", "Mini-games"))
        .addSeparator()
        
        // Privacy Settings
        .addLabel("Privacy Settings")
        .addToggle("showOnlineStatus", "Show online status to others", true)
        .addToggle("allowFriendRequests", "Allow friend requests", true)
        .addToggle("showStats", "Show statistics publicly", false)
        .addDropdown("profileVisibility", "Profile Visibility:", 
                    Arrays.asList("Public", "Friends Only", "Private"), 0)
        .addSeparator()
        
        // Notification Preferences
        .addLabel("Notifications")
        .addToggle("emailNotifications", "Email notifications", false)
        .addToggle("friendOnlineNotifications", "Friend online notifications", true)
        .addToggle("eventNotifications", "Server event notifications", true)
        
        // Validation
        .addFormValidator(response -> {
            String displayName = response.getInput("displayName").trim();
            if (displayName.isEmpty()) {
                return "Display name cannot be empty";
            }
            if (displayName.length() < 3 || displayName.length() > 20) {
                return "Display name must be 3-20 characters long";
            }
            if (!displayName.matches("[a-zA-Z0-9_\\s]+")) {
                return "Display name can only contain letters, numbers, spaces, and underscores";
            }
            if (isDisplayNameTaken(displayName, player)) {
                return "Display name is already taken";
            }
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            try {
                // Create player profile
                PlayerProfile profile = createPlayerProfile(player, response);
                savePlayerProfile(profile);
                
                player.sendMessage("Profile created successfully!");
                player.sendMessage("Welcome to the server, " + response.getInput("displayName") + "!");
                
                // Show welcome tour or next steps
                showWelcomeTour(player);
                
            } catch (Exception e) {
                player.sendMessage("Error creating profile: " + e.getMessage());
                getLogger().severe("Profile creation failed for " + player.getName() + ": " + e.getMessage());
            }
        } else {
            player.sendMessage("Profile creation cancelled.");
            // Maybe show a simplified quick setup instead
            showQuickSetup(player);
        }
    });
}

private PlayerProfile createPlayerProfile(Player player, CustomFormResponse response) {
    PlayerProfile profile = new PlayerProfile(player.getUniqueId());
    
    profile.setDisplayName(response.getInput("displayName"));
    profile.setBio(response.getInput("bio"));
    profile.setTimezone(response.getDropdown("timezone"));
    profile.setFavoriteGamemode(response.getDropdown("favoriteGamemode"));
    profile.setSkillLevel((int) response.getSlider("skillLevel"));
    profile.setInterests(response.getMultiSelect("interests"));
    profile.setShowOnlineStatus(response.getToggle("showOnlineStatus"));
    profile.setAllowFriendRequests(response.getToggle("allowFriendRequests"));
    profile.setShowStats(response.getToggle("showStats"));
    profile.setProfileVisibility(response.getDropdown("profileVisibility"));
    profile.setEmailNotifications(response.getToggle("emailNotifications"));
    profile.setFriendOnlineNotifications(response.getToggle("friendOnlineNotifications"));
    profile.setEventNotifications(response.getToggle("eventNotifications"));
    
    return profile;
}
```

### Example 3: Shop Creation Form

```java
public void showShopCreation(Player player) {
    if (!player.hasPermission("shops.create")) {
        player.sendMessage("You don't have permission to create shops.");
        return;
    }
    
    // Check if player has reached shop limit
    int currentShops = getPlayerShopCount(player);
    int maxShops = getMaxShopsForPlayer(player);
    
    if (currentShops >= maxShops) {
        player.sendMessage(String.format("You have reached your shop limit (%d/%d)", currentShops, maxShops));
        return;
    }
    
    CustomFormBuilder form = new CustomFormBuilder()
        .title("Create New Shop")
        
        // Basic Shop Information
        .addLabel("Shop Information")
        .addInput("shopName", "Shop Name:", "Enter a unique shop name", "")
        .addTextArea("description", "Description:", "Describe what your shop sells (optional)")
        .addDropdown("category", "Shop Category:", 
                    Arrays.asList("General", "Food", "Tools", "Weapons", "Armor", 
                                "Building", "Redstone", "Decorative", "Other"), 0)
        .addSeparator()
        
        // Location Settings
        .addLabel("Location Settings")
        .addToggle("useCurrentLocation", "Use current location", true)
        .addConditionalGroup("customLocation", "useCurrentLocation", false, group -> {
            group.addNumber("locationX", "X Coordinate:", "Enter X coordinate", 0.0);
            group.addNumber("locationY", "Y Coordinate:", "Enter Y coordinate", 64.0);
            group.addNumber("locationZ", "Z Coordinate:", "Enter Z coordinate", 0.0);
            group.addDropdown("world", "World:", getWorldNames(), 0);
        })
        .addSeparator()
        
        // Shop Settings
        .addLabel("Shop Settings")
        .addToggle("isPublic", "Public Shop", true, "Allow other players to find and visit your shop")
        .addToggle("allowVisitors", "Allow Visitors", true, "Let players browse without buying")
        .addSlider("shopSize", "Shop Size:", 1, 5, 1, 3,
                  value -> {
                      String[] sizes = {"Tiny (9 items)", "Small (18 items)", "Medium (27 items)", 
                                      "Large (36 items)", "Huge (45 items)"};
                      return sizes[(int)value - 1];
                  })
        .addSeparator()
        
        // Economic Settings
        .addLabel("Economic Settings")
        .addNumber("setupCost", "Setup Cost:", "Cost to create this shop", getShopSetupCost(player))
        .addSlider("taxRate", "Tax Rate (%):", 0.0, 10.0, 0.5, 2.0,
                  value -> String.format("%.1f%% (you keep %.1f%%)", value, 100 - value))
        .addToggle("autoRestock", "Auto-restock from inventory", false)
        .addConditionalGroup("restockSettings", "autoRestock", true, group -> {
            group.addIntSlider("restockInterval", "Restock Interval (minutes):", 5, 60, 5, 15);
            group.addToggle("notifyLowStock", "Notify when stock is low", true);
        })
        
        // Validation
        .addFormValidator(response -> {
            String shopName = response.getInput("shopName").trim();
            if (shopName.isEmpty()) {
                return "Shop name cannot be empty";
            }
            if (shopName.length() < 3 || shopName.length() > 30) {
                return "Shop name must be 3-30 characters long";
            }
            if (isShopNameTaken(shopName)) {
                return "Shop name is already taken";
            }
            
            double setupCost = response.getNumber("setupCost");
            if (getPlayerBalance(player) < setupCost) {
                return String.format("Insufficient funds. You need $%.2f but only have $%.2f", 
                                   setupCost, getPlayerBalance(player));
            }
            
            return null;
        });
    
    Easy4FormAPI.sendCustomForm(player, form, response -> {
        if (response != null) {
            try {
                // Create the shop
                Shop shop = createShop(player, response);
                
                // Charge setup cost
                double setupCost = response.getNumber("setupCost");
                removePlayerBalance(player, setupCost);
                
                player.sendMessage(String.format("Shop '%s' created successfully!", shop.getName()));
                player.sendMessage(String.format("Setup cost: $%.2f", setupCost));
                player.sendMessage("Use /shop manage to add items to your shop.");
                
                // Teleport to shop location if requested
                if (response.getToggle("useCurrentLocation")) {
                    player.sendMessage("Your shop has been created at your current location.");
                } else {
                    player.sendMessage("Type /shop visit " + shop.getName() + " to visit your new shop.");
                }
                
                // Log shop creation
                getLogger().info(String.format("Player %s created shop '%s' at %s", 
                                              player.getName(), shop.getName(), shop.getLocation()));
                
            } catch (Exception e) {
                player.sendMessage("Error creating shop: " + e.getMessage());
                getLogger().severe("Shop creation failed: " + e.getMessage());
            }
        } else {
            player.sendMessage("Shop creation cancelled.");
        }
    });
}

private Shop createShop(Player player, CustomFormResponse response) {
    Shop shop = new Shop();
    
    shop.setOwner(player.getUniqueId());
    shop.setName(response.getInput("shopName"));
    shop.setDescription(response.getInput("description"));
    shop.setCategory(response.getDropdown("category"));
    
    // Set location
    if (response.getToggle("useCurrentLocation")) {
        shop.setLocation(player.getLocation());
    } else {
        double x = response.getNumber("locationX");
        double y = response.getNumber("locationY");
        double z = response.getNumber("locationZ");
        String worldName = response.getDropdown("world");
        World world = Bukkit.getWorld(worldName);
        shop.setLocation(new Location(world, x, y, z));
    }
    
    shop.setPublic(response.getToggle("isPublic"));
    shop.setAllowVisitors(response.getToggle("allowVisitors"));
    shop.setSize((int) response.getSlider("shopSize"));
    shop.setTaxRate(response.getSlider("taxRate"));
    shop.setAutoRestock(response.getToggle("autoRestock"));
    
    if (response.getToggle("autoRestock")) {
        shop.setRestockInterval((int) response.getSlider("restockInterval"));
        shop.setNotifyLowStock(response.getToggle("notifyLowStock"));
    }
    
    shop.save();
    return shop;
}
```

## Troubleshooting

### Common Issues

**Form not displaying:**
- Ensure player is a Bedrock player
- Check if Floodgate is properly installed
- Verify Easy4Form is enabled
- Check for form size limits (too many elements)

**Response values are null or incorrect:**
- Verify element IDs match between form creation and response handling
- Check if form was submitted or closed
- Use debugging to log response values
- Ensure proper type casting for numeric values

**Validation not working:**
- Check validation logic for edge cases
- Ensure validation functions return null for valid input
- Test with various input combinations
- Log validation results for debugging

**Performance issues with large forms:**
- Consider breaking large forms into multiple steps
- Use conditional groups to hide irrelevant sections
- Limit the number of dropdown options
- Optimize validation logic

### Debug Example

```java
CustomFormBuilder form = new CustomFormBuilder()
    .title("Debug Form")
    .addInput("testInput", "Test Input:", "Enter text")
    .addToggle("testToggle", "Test Toggle", false)
    .addDropdown("testDropdown", "Test Dropdown:", 
                Arrays.asList("Option 1", "Option 2", "Option 3"), 0)
    .addSlider("testSlider", "Test Slider:", 0, 100, 10, 50);

Easy4FormAPI.sendCustomForm(player, form, response -> {
    if (response == null) {
        getLogger().info("Form was closed by " + player.getName());
        return;
    }
    
    // Log all response values for debugging
    getLogger().info("Form response from " + player.getName() + ":");
    getLogger().info("  Input: '" + response.getInput("testInput") + "'");
    getLogger().info("  Toggle: " + response.getToggle("testToggle"));
    getLogger().info("  Dropdown: '" + response.getDropdown("testDropdown") + "' (index: " + 
                    response.getDropdownIndex("testDropdown") + ")");
    getLogger().info("  Slider: " + response.getSlider("testSlider"));
    
    player.sendMessage("Form submitted successfully! Check console for details.");
});
```

## Related Documentation

- **[Simple Forms](simple-forms.md)** - For menu navigation and single choices
- **[Modal Forms](modal-forms.md)** - For yes/no confirmations
- **[Main API Documentation](README.md)** - Overview and getting started
- **[Player Utilities](README.md#player-utilities)** - Helper methods

---

**Next Steps:**
- Try the [Simple Forms guide](simple-forms.md) for menu creation
- Explore [Modal Forms](modal-forms.md) for confirmations
- Return to [Main API Documentation](README.md) for other features