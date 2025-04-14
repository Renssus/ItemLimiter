package me.renssus.itemLimister;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryLimiter implements Listener {

    private final Map<Material, Integer> limits = new HashMap<>();
    private final File customConfigFile;
    private FileConfiguration customConfig;

    public InventoryLimiter(JavaPlugin plugin) {
        this.customConfigFile = new File(plugin.getDataFolder(), "LimitOnItem.yml");
        loadCustomConfig(plugin);
        loadLimitsFromConfig();
    }

    private void loadCustomConfig(JavaPlugin plugin) {
        if (!customConfigFile.exists()) {
            plugin.saveResource("LimitOnItem.yml", false);
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    private void loadLimitsFromConfig() {
        if (customConfig.isConfigurationSection("limits")) {
            for (String key : customConfig.getConfigurationSection("limits").getKeys(false)) {
                try {
                    Material material = Material.valueOf(key.toUpperCase());
                    int amount = customConfig.getInt("limits." + key);
                    limits.put(material, amount);
                } catch (IllegalArgumentException e) {
                    System.out.println("[ItemLimiter] Ongeldig materiaal in LimitOnItem.yml: " + key);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Material type = item.getType();

        if (!limits.containsKey(type)) return;

        int maxAllowed = limits.get(type);
        int currentAmount = 0;

        for (ItemStack i : event.getPlayer().getInventory().getContents()) {
            if (i != null && i.getType() == type) {
                currentAmount += i.getAmount();
            }
        }

        int amountLeft = maxAllowed - currentAmount;

        if (amountLeft <= 0) {
            event.setCancelled(true);
            event.getPlayer().sendActionBar("§cMaximaal " + maxAllowed + " van " + type.name().toLowerCase() + " toegestaan!");
        } else if (item.getAmount() > amountLeft) {
            event.setCancelled(true);

            ItemStack allowedStack = item.clone();
            allowedStack.setAmount(amountLeft);

            event.getPlayer().getInventory().addItem(allowedStack);

            item.setAmount(item.getAmount() - amountLeft);
            event.getItem().setItemStack(item);

            event.getPlayer().sendActionBar("§eJe hebt er " + amountLeft + " van " + type.name().toLowerCase() + " opgepakt (limiet " + maxAllowed + ")");
        }
    }

}
