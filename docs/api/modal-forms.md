# Modal Forms - Easy4Form Main Proxy API

**Language**: [English](modal-forms.md) | [中文](modal-forms_zh.md)

**Back to**: [Main API Documentation](README.md)

Modal Forms are dialog-style forms that present users with two choices, typically "Yes/No", "Confirm/Cancel", or similar binary decisions. They're perfect for confirmations, warnings, and any scenario requiring a simple binary choice.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Advanced Features](#advanced-features)
- [Builder Pattern](#builder-pattern)
- [Best Practices](#best-practices)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Overview

### What are Modal Forms?

Modal Forms display:
- A **title** at the top
- **content text** explaining the situation or question
- **Two buttons**: typically representing "Yes/No", "Confirm/Cancel", etc.
- Returns a **boolean** value: `true` for the first button, `false` for the second

### When to Use Modal Forms

✅ **Perfect for:**
- Confirmation dialogs ("Are you sure?")
- Yes/No questions
- Accept/Decline prompts
- Warning acknowledgments
- Binary choices

❌ **Not ideal for:**
- Multiple choice selections (use [Simple Forms](simple-forms.md))
- Data input (use [Custom Forms](custom-forms.md))
- Navigation menus (use [Simple Forms](simple-forms.md))

## Basic Usage

### Simple Example

```java
import cn.enderrealm.easy4form.api.Easy4FormAPI;

public void confirmPlayerKick(Player admin, Player target) {
    Easy4FormAPI.sendModalForm(
        admin,
        "Confirm Action",                           // Title
        String.format("Are you sure you want to kick %s?", // Content
                     target.getName()),
        "Yes, Kick",                               // True button (first button)
        "Cancel",                                  // False button (second button)
        response -> {                              // Response handler
            if (response != null) {
                if (response) {
                    // User clicked "Yes, Kick" (true)
                    target.kickPlayer("You have been kicked by " + admin.getName());
                    admin.sendMessage("Player " + target.getName() + " has been kicked.");
                } else {
                    // User clicked "Cancel" (false)
                    admin.sendMessage("Kick cancelled.");
                }
            } else {
                // Form was closed without clicking either button
                admin.sendMessage("Action cancelled.");
            }
        }
    );
}
```

### Response Handling

The response handler receives a `Boolean` that can be:
- **true**: The first button (typically "Yes", "Confirm", "Accept") was clicked
- **false**: The second button (typically "No", "Cancel", "Decline") was clicked
- **null**: The form was closed without clicking either button

```java
response -> {
    if (response == null) {
        // Form was closed without selection
        player.sendMessage("No selection made.");
        return;
    }
    
    if (response) {
        // First button clicked (true)
        handleConfirmation(player);
    } else {
        // Second button clicked (false)
        handleCancellation(player);
    }
}
```

## Advanced Features

### Dynamic Content

```java
public void confirmPurchase(Player player, String itemName, double price) {
    double playerBalance = getPlayerBalance(player);
    
    String content;
    String confirmButton;
    
    if (playerBalance >= price) {
        content = String.format(
            "Purchase %s for $%.2f?\n\nYour balance: $%.2f\nAfter purchase: $%.2f",
            itemName, price, playerBalance, playerBalance - price
        );
        confirmButton = "Buy Now";
    } else {
        content = String.format(
            "Insufficient funds!\n\n%s costs $%.2f\nYour balance: $%.2f\nYou need $%.2f more",
            itemName, price, playerBalance, price - playerBalance
        );
        confirmButton = "OK";
    }
    
    Easy4FormAPI.sendModalForm(
        player,
        "Purchase Confirmation",
        content,
        confirmButton,
        "Cancel",
        response -> {
            if (response != null && response && playerBalance >= price) {
                // Process purchase
                processPurchase(player, itemName, price);
            } else if (response != null && !response) {
                player.sendMessage("Purchase cancelled.");
            }
        }
    );
}
```

### Error Handling with Retry

```java
public void confirmDangerousAction(Player player, String action, int attempts) {
    if (attempts > 3) {
        player.sendMessage("Too many attempts. Action cancelled for safety.");
        return;
    }
    
    Easy4FormAPI.sendModalForm(
        player,
        "Dangerous Action",
        String.format(
            "WARNING: This action cannot be undone!\n\n" +
            "Action: %s\n\n" +
            "Are you absolutely sure you want to continue?\n" +
            "(Attempt %d/3)",
            action, attempts
        ),
        "Yes, I'm Sure",
        "Cancel",
        response -> {
            try {
                if (response == null) {
                    player.sendMessage("Action cancelled.");
                } else if (response) {
                    // Perform the dangerous action
                    performDangerousAction(player, action);
                } else {
                    player.sendMessage("Action cancelled.");
                }
            } catch (Exception e) {
                player.sendMessage("An error occurred. Please try again.");
                getLogger().severe("Error in dangerous action: " + e.getMessage());
                
                // Retry with incremented attempt counter
                confirmDangerousAction(player, action, attempts + 1);
            }
        }
    );
}
```

## Builder Pattern

For more complex modal forms, use the builder pattern:

```java
import cn.enderrealm.easy4form.api.ModalFormBuilder;

public void showAdvancedConfirmation(Player player) {
    new ModalFormBuilder()
        .title("Advanced Confirmation")
        .content("This is a complex decision that requires careful consideration.")
        .trueButton("I Accept")
        .falseButton("I Decline")
        .responseHandler(response -> {
            if (response != null) {
                handleAdvancedResponse(player, response);
            }
        })
        .errorHandler(error -> {
            player.sendMessage("An error occurred: " + error.getMessage());
        })
        .send(player);
}
```

### Builder Methods

```java
ModalFormBuilder builder = new ModalFormBuilder()
    .title("Form Title")                    // Set form title
    .content("Form content and question")   // Set form content
    .trueButton("Yes")                      // Set first button text (returns true)
    .falseButton("No")                      // Set second button text (returns false)
    .responseHandler(response -> { ... })   // Set response handler
    .errorHandler(error -> { ... });       // Set error handler (optional)

// Send the form
builder.send(player);
```

## Best Practices

### 1. Use Clear, Action-Oriented Button Text

```java
// Good - Clear and specific
Easy4FormAPI.sendModalForm(
    player,
    "Delete Item",
    "Are you sure you want to delete this item? This cannot be undone.",
    "Delete Forever",  // Clear consequence
    "Keep Item",       // Clear alternative
    handler
);

// Avoid - Generic and unclear
Easy4FormAPI.sendModalForm(
    player,
    "Confirmation",
    "Do you want to proceed?",
    "Yes",    // Unclear what "yes" means
    "No",     // Unclear what "no" means
    handler
);
```

### 2. Provide Context in Content

```java
// Good - Provides full context
String content = String.format(
    "You are about to teleport to %s.\n\n" +
    "This will cost $%.2f and has a 10-second cooldown.\n\n" +
    "Continue with teleportation?",
    destination, cost
);

// Avoid - Lacks context
String content = "Teleport?";
```

### 3. Handle All Response Cases

```java
response -> {
    if (response == null) {
        // Always handle form closure
        player.sendMessage("Action cancelled.");
    } else if (response) {
        // Handle confirmation
        confirmAction(player);
    } else {
        // Handle cancellation
        cancelAction(player);
    }
}
```

### 4. Use Appropriate Titles

```java
// Good - Descriptive titles
"Confirm Purchase"
"Delete Warning"
"Teleport Request"
"Permission Required"

// Avoid - Generic titles
"Confirmation"
"Question"
"Dialog"
"Form"
```

### 5. Consider Consequences

```java
// For irreversible actions, make it clear
Easy4FormAPI.sendModalForm(
    player,
    "Permanent Action",
    "WARNING: This action cannot be undone!\n\n" +
    "All your progress will be lost.\n\n" +
    "Are you absolutely certain?",
    "Yes, Delete Everything",
    "No, Keep My Progress",
    handler
);
```

## Examples

### Example 1: Server Rules Acceptance

```java
public void showRulesAcceptance(Player player) {
    String rules = 
        "Server Rules:\n\n" +
        "1. No griefing or destroying others' builds\n" +
        "2. Be respectful to all players\n" +
        "3. No cheating or exploiting\n" +
        "4. Follow staff instructions\n\n" +
        "By clicking 'I Accept', you agree to follow these rules.";
    
    Easy4FormAPI.sendModalForm(
        player,
        "Server Rules",
        rules,
        "I Accept",
        "I Decline",
        response -> {
            if (response == null) {
                // Player closed form - ask again after delay
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    showRulesAcceptance(player);
                }, 100L); // 5 seconds
            } else if (response) {
                // Rules accepted
                setPlayerRulesAccepted(player, true);
                player.sendMessage("Welcome to the server! Enjoy your stay.");
                teleportToSpawn(player);
            } else {
                // Rules declined
                player.kickPlayer("You must accept the server rules to play.");
            }
        }
    );
}
```

### Example 2: Economy Transaction

```java
public void confirmTransaction(Player sender, Player receiver, double amount) {
    double senderBalance = getPlayerBalance(sender);
    
    if (senderBalance < amount) {
        sender.sendMessage("Insufficient funds!");
        return;
    }
    
    String content = String.format(
        "Send $%.2f to %s?\n\n" +
        "Your current balance: $%.2f\n" +
        "After transaction: $%.2f\n\n" +
        "This action cannot be undone.",
        amount, receiver.getName(), senderBalance, senderBalance - amount
    );
    
    Easy4FormAPI.sendModalForm(
        sender,
        "Confirm Payment",
        content,
        "Send Money",
        "Cancel",
        response -> {
            if (response != null && response) {
                // Double-check balance (in case it changed)
                if (getPlayerBalance(sender) >= amount) {
                    // Process transaction
                    removePlayerBalance(sender, amount);
                    addPlayerBalance(receiver, amount);
                    
                    sender.sendMessage(String.format("Sent $%.2f to %s", amount, receiver.getName()));
                    receiver.sendMessage(String.format("Received $%.2f from %s", amount, sender.getName()));
                    
                    // Log transaction
                    logTransaction(sender, receiver, amount);
                } else {
                    sender.sendMessage("Transaction failed: Insufficient funds.");
                }
            } else if (response != null) {
                sender.sendMessage("Transaction cancelled.");
            }
        }
    );
}
```

### Example 3: PvP Challenge

```java
public void sendPvPChallenge(Player challenger, Player target) {
    // First, ask the challenger to confirm
    Easy4FormAPI.sendModalForm(
        challenger,
        "PvP Challenge",
        String.format("Challenge %s to a PvP duel?\n\nBoth players will be teleported to the arena.", 
                     target.getName()),
        "Send Challenge",
        "Cancel",
        response -> {
            if (response != null && response) {
                // Send challenge to target
                sendChallengeToTarget(challenger, target);
            } else if (response != null) {
                challenger.sendMessage("Challenge cancelled.");
            }
        }
    );
}

private void sendChallengeToTarget(Player challenger, Player target) {
    Easy4FormAPI.sendModalForm(
        target,
        "PvP Challenge Received",
        String.format("%s has challenged you to a PvP duel!\n\n" +
                     "Do you accept this challenge?", challenger.getName()),
        "Accept Duel",
        "Decline",
        response -> {
            if (response != null && response) {
                // Challenge accepted
                challenger.sendMessage(target.getName() + " accepted your challenge!");
                target.sendMessage("Challenge accepted! Preparing arena...");
                startPvPDuel(challenger, target);
            } else if (response != null) {
                // Challenge declined
                challenger.sendMessage(target.getName() + " declined your challenge.");
                target.sendMessage("Challenge declined.");
            } else {
                // No response
                challenger.sendMessage(target.getName() + " did not respond to your challenge.");
            }
        }
    );
}
```

### Example 4: Admin Confirmation

```java
public void confirmAdminAction(Player admin, String action, Runnable actionToPerform) {
    // Check if player has admin permissions
    if (!admin.hasPermission("server.admin")) {
        admin.sendMessage("You don't have permission for this action.");
        return;
    }
    
    Easy4FormAPI.sendModalForm(
        admin,
        "🔐 Admin Action Required",
        String.format(
            "ADMIN CONFIRMATION REQUIRED\n\n" +
            "Action: %s\n\n" +
            "This action will affect the server and/or players.\n" +
            "Please confirm that you want to proceed.\n\n" +
            "Logged as: %s",
            action, admin.getName()
        ),
        "✅ Confirm & Execute",
        "❌ Cancel",
        response -> {
            if (response != null && response) {
                try {
                    // Log admin action
                    getLogger().info(String.format("Admin %s executed: %s", admin.getName(), action));
                    
                    // Perform the action
                    actionToPerform.run();
                    
                    admin.sendMessage("✅ Admin action executed successfully.");
                } catch (Exception e) {
                    admin.sendMessage("❌ Error executing admin action: " + e.getMessage());
                    getLogger().severe("Admin action failed: " + e.getMessage());
                }
            } else if (response != null) {
                admin.sendMessage("Admin action cancelled.");
            }
        }
    );
}

// Usage example
public void clearAllPlayerInventories(Player admin) {
    confirmAdminAction(admin, "Clear all player inventories", () -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.sendMessage("Your inventory has been cleared by an administrator.");
        }
    });
}
```

## Troubleshooting

### Common Issues

**Form not displaying:**
- Ensure player is a Bedrock player
- Check if Floodgate is properly installed
- Verify Easy4Form is enabled

**Unexpected response values:**
- Remember: `true` = first button, `false` = second button, `null` = closed
- Check your boolean logic in the response handler
- Use debugging to log response values

**Button text not showing correctly:**
- Ensure button text is not null or empty
- Check for special characters that might not display properly
- Keep button text reasonably short

### Debug Example

```java
Easy4FormAPI.sendModalForm(
    player,
    "Debug Modal",
    "This is a test modal form.",
    "True Button",
    "False Button",
    response -> {
        // Log the response for debugging
        getLogger().info(String.format("Player %s modal response: %s", 
                                      player.getName(), 
                                      response != null ? response.toString() : "null"));
        
        if (response == null) {
            player.sendMessage("Form was closed");
        } else if (response) {
            player.sendMessage("You clicked the TRUE button");
        } else {
            player.sendMessage("You clicked the FALSE button");
        }
    }
);
```

## Related Documentation

- **[Simple Forms](simple-forms.md)** - For multiple choice selections
- **[Custom Forms](custom-forms.md)** - For data input
- **[Main API Documentation](README.md)** - Overview and getting started
- **[Player Utilities](README.md#player-utilities)** - Helper methods

---

**Next Steps:**
- Try the [Simple Forms guide](simple-forms.md) for menu creation
- Explore [Custom Forms](custom-forms.md) for advanced input collection
- Return to [Main API Documentation](README.md) for other features