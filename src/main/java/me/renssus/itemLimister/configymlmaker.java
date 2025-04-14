package me.renssus.itemLimister;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;

public class configymlmaker {

    private final JavaPlugin plugin;

    public configymlmaker(JavaPlugin plugin) {
        this.plugin = plugin;
        createFileIfNotExists("LimitOnItem.yml");
    }

    private void createFileIfNotExists(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            try {
                file.createNewFile();
                getLogger().info(fileName + " created successfully.");
            } catch (IOException e) {
                getLogger().severe("Could not create " + fileName);
                e.printStackTrace();
            }
        }
    }
}
