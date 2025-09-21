# Easy4Form API Documentation

Easy4Form is a simplified Form API for Floodgate, making it easier to create and send forms to Bedrock players from your Java server.

## Table of Contents

- [Introduction](#introduction)
- [Player Utilities](#player-utilities)
- [Simple Forms](#simple-forms)
- [Modal Forms](#modal-forms)
- [Custom Forms](#custom-forms)

## Introduction

Easy4Form provides a simplified interface for creating and sending forms to Bedrock players through the Floodgate API. It supports all three types of forms available in Bedrock Edition:

- **Simple Forms**: A list of buttons that the player can click
- **Modal Forms**: A dialog with two buttons (yes/no, confirm/cancel, etc.)
- **Custom Forms**: A form with various input elements (text fields, toggles, sliders, dropdowns, etc.)

## Player Utilities

### Check if a player is a Bedrock player

```java
boolean isBedrockPlayer = Easy4FormAPI.isBedrockPlayer(player);
```

**Example:**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;

public void onPlayerJoin(Player player) {
    if (Easy4FormAPI.isBedrockPlayer(player)) {
        player.sendMessage("Welcome, Bedrock player!");
    } else {
        player.sendMessage("Welcome, Java player!");
    }
}
```

### Get the FloodgatePlayer instance

```java
FloodgatePlayer floodgatePlayer = Easy4FormAPI.getFloodgatePlayer(player);
```

**Example:**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.Easy4FormAPI;
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
import cn.enderrealm.easy4form.api.Easy4FormAPI;
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

```java
new SimpleFormBuilder()
    .title("Title")
    .content("Content")
    .button("Button 1")                                     // Button without image
    .button("Button 2", "textures/items/diamond")          // Button with resource pack image
    .buttonWithUrl("Button 3", "https://example.com/image.png") // Button with URL image
    .responseHandler(response -> {
        if (response != null) {
            player.sendMessage("You clicked button: " + response);
        }
    })
    .send(player);
```

**Button Methods:**
- `button(String text)` - Add a regular button without an image
- `button(String text, String imagePath)` - Add a button with a resource pack image, using Minecraft resource pack path format
- `buttonWithUrl(String text, String imageUrl)` - Add a button with a URL image, using a web image URL

**Example: Shop Categories**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.SimpleFormBuilder;

public void showShopCategories(Player player) {
    new SimpleFormBuilder()
        .title("Shop")
        .content("Select a category:")
        .button("Weapons")
        .button("Armor")
        .button("Tools")
        .button("Food")
        .button("Miscellaneous")
        .responseHandler(response -> {
            if (response == null) {
                return; // Form was closed
            }
            
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
        .send(player);
}
```

## Modal Forms

Modal forms consist of a title, content, and two buttons (typically for yes/no or confirm/cancel scenarios).

### Using the Easy4FormAPI

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
import cn.enderrealm.easy4form.api.Easy4FormAPI;

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

```java
new ModalFormBuilder()
    .title("Confirmation")
    .content("Are you sure you want to proceed?")
    .button1("Yes")
    .button2("No")
    .responseHandler(response -> {
        if (response != null) {
            if (response) {
                player.sendMessage("You confirmed!");
            } else {
                player.sendMessage("You cancelled!");
            }
        }
    })
    .send(player);
```

**Example: Delete Home Confirmation**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.ModalFormBuilder;

public void confirmDeleteHome(Player player, String homeName) {
    new ModalFormBuilder()
        .title("Delete Home")
        .content("Are you sure you want to delete your home \"" + homeName + "\"?")
        .button1("Delete")
        .button2("Cancel")
        .responseHandler(response -> {
            if (response != null && response) {
                // Code to delete the home
                player.sendMessage("Home \"" + homeName + "\" has been deleted.");
            }
        })
        .send(player);
}
```

## Custom Forms

Custom forms allow you to create forms with various input elements such as text fields, toggles, sliders, dropdowns, etc.

### Using the Easy4FormAPI and CustomFormBuilder

```java
CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "Settings", response -> {
    if (response != null) {
        String name = (String) response.get("name");
        boolean notifications = (boolean) response.get("notifications");
        int volume = ((Float) response.get("volume")).intValue();
        String difficulty = (String) response.get("difficulty");
        
        player.sendMessage("Name: " + name);
        player.sendMessage("Notifications: " + notifications);
        player.sendMessage("Volume: " + volume);
        player.sendMessage("Difficulty: " + difficulty);
    }
});

form.label("info", "Please configure your settings below:")
    .input("name", "Name", "Enter your name", player.getName())
    .toggle("notifications", "Enable Notifications", true)
    .slider("volume", "Volume", 0, 100, 10, 50)
    .dropdown("difficulty", "Difficulty", Arrays.asList("Easy", "Normal", "Hard"), 1)
    .send(player);
```

**Example: Player Profile Form**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.CustomFormBuilder;
import cn.enderrealm.easy4form.api.Easy4FormAPI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public void showProfileForm(Player player) {
    List<String> genders = Arrays.asList("Male", "Female", "Other", "Prefer not to say");
    List<String> ageRanges = Arrays.asList("Under 18", "18-24", "25-34", "35-44", "45+", "Prefer not to say");
    
    CustomFormBuilder form = Easy4FormAPI.createCustomForm(player, "Player Profile", response -> {
        if (response == null) {
            return; // Form was closed
        }
        
        // Save the profile information
        String nickname = (String) response.get("nickname");
        String bio = (String) response.get("bio");
        boolean showProfile = (boolean) response.get("showProfile");
        String gender = genders.get(((Float) response.get("gender")).intValue());
        String ageRange = ageRanges.get(((Float) response.get("ageRange")).intValue());
        
        // Code to save the profile information
        player.sendMessage("Profile updated successfully!");
    });
    
    form.label("header", "Please fill out your profile information:")
        .input("nickname", "Nickname", "Enter your preferred nickname", player.getName())
        .input("bio", "Biography", "Tell us about yourself", "")
        .toggle("showProfile", "Show my profile to other players", true)
        .dropdown("gender", "Gender", genders, 0)
        .dropdown("ageRange", "Age Range", ageRanges, 0)
        .send(player);
}
```

### Using the CustomFormBuilder Directly

```java
new CustomFormBuilder()
    .title("Settings")
    .label("info", "Please configure your settings below:")
    .input("name", "Name", "Enter your name", player.getName())
    .toggle("notifications", "Enable Notifications", true)
    .slider("volume", "Volume", 0, 100, 10, 50)
    .dropdown("difficulty", "Difficulty", Arrays.asList("Easy", "Normal", "Hard"), 1)
    .responseHandler(response -> {
        if (response != null) {
            String name = (String) response.get("name");
            boolean notifications = (boolean) response.get("notifications");
            int volume = ((Float) response.get("volume")).intValue();
            String difficulty = (String) response.get("difficulty");
            
            player.sendMessage("Settings saved!");
        }
    })
    .send(player);
```

**Example: Server Feedback Form**
```java
import org.bukkit.entity.Player;
import cn.enderrealm.easy4form.api.CustomFormBuilder;
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
        .responseHandler(response -> {
            if (response == null) {
                return; // Form was closed
            }
            
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
        .send(player);
}

private List<Player> getAdminPlayers() {
    // Code to get online admin players
    return new ArrayList<>();
}
```

## Additional Notes

- All form builders use a fluent interface, allowing method chaining for cleaner code.
- Response handlers are called asynchronously, so be careful when interacting with the Bukkit API.
- If a player closes a form without selecting an option, the response will be `null`.
- For custom forms, the response is a map of component IDs to their values.
- The API automatically checks if a player is a Bedrock player before sending forms.

---

For more information, please refer to the [Floodgate documentation](https://wiki.geysermc.org/floodgate/) and the [Cumulus API documentation](https://github.com/GeyserMC/Cumulus).