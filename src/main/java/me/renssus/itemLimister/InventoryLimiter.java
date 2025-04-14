package me.renssus.itemLimister;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class InventoryLimiter implements Listener {

    private final File customConfigFile;
    private final JavaPlugin plugin;

    public InventoryLimiter(JavaPlugin plugin) {
        this.plugin = plugin;
        this.customConfigFile = new File(plugin.getDataFolder(), "LimitOnItem.yml");
    }

    private int getLimit(Material material) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(customConfigFile);
        String key = material.name().toUpperCase();
        if (config.isSet("limits." + key)) {
            return config.getInt("limits." + key);
        }
        return -1; // No limit
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Material type = item.getType();

        int maxAllowed = getLimit(type);
        if (maxAllowed == -1) return;

        Player player = event.getPlayer();

        // Check if the player has their inventory open and cancel pick up item
        if (player.getOpenInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            player.sendActionBar("§cYou cannot pick up " + type.name().toLowerCase() + ", you already have the maximum amount!");
            return;
        }

        // If the player is NOT in their inventory, we process the pickup
        int currentAmount = 0;

        for (ItemStack i : player.getInventory().getContents()) {
            if (i != null && i.getType() == type) {
                currentAmount += i.getAmount();
            }
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand != null && inHand.getType() == type) {
            currentAmount += inHand.getAmount();
        }

        if (currentAmount >= maxAllowed) {
            event.setCancelled(true);
            player.sendActionBar("§cYou cannot pick up more " + type.name().toLowerCase() + ", you already have the maximum amount!");
        } else {
            int amountLeft = maxAllowed - currentAmount;

            if (item.getAmount() > amountLeft) {
                event.setCancelled(true);

                ItemStack allowedStack = item.clone();
                allowedStack.setAmount(amountLeft);
                player.getInventory().addItem(allowedStack);

                item.setAmount(item.getAmount() - amountLeft);
                event.getItem().setItemStack(item);
            }
        }
    }

    @EventHandler
    public void onItemMoveToShulkerBoxOrEnderChest(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            ItemStack item = event.getCurrentItem();
            Inventory inventory = event.getInventory();

            if (item == null || item.getType() == Material.AIR) return;
            int maxAllowed = getLimit(item.getType());
            if (maxAllowed == -1) return;

            if (inventory.getType() == InventoryType.SHULKER_BOX || inventory.getType() == InventoryType.ENDER_CHEST) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage("§cYou cannot place " + item.getType().name().toLowerCase() + " in a " + inventory.getType().name().toLowerCase() + " because it is limited.");
            }
        }
    }
}
