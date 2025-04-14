package me.renssus.itemLimister;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ItemLimister extends JavaPlugin {

    @Override
    public void onEnable() {
        new configymlmaker(this);
        getServer().getPluginManager().registerEvents(new InventoryLimiter(this), this);
    }

    @Override
    public void onDisable() {
    }
}
