package me.renssus.itemLimister;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

                // Load configuration and add default content
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // Add a comment at the top (manually, since YamlConfiguration doesn't support true header comments)
                config.set("limits.GOLDEN_APPLE", 5);
                config.set("limits.ENDER_PEARL", 16);
                config.set("limits.TNT", 10);

                config.save(file);

                // Now manually prepend the file with a comment
                prependCommentToFile(file,
                        "# Material names must be in ALL CAPS.\n" +
                                "# Example default values below. Adjust to your needs!\n"
                );

            } catch (IOException e) {
                getLogger().severe("Could not create " + fileName);
                e.printStackTrace();
            }
        }
    }

    private void prependCommentToFile(File file, String comment) {
        try {
            String original = java.nio.file.Files.readString(file.toPath());
            String updated = comment + original;
            java.nio.file.Files.writeString(file.toPath(), updated);
        } catch (IOException e) {
            getLogger().warning("Could not prepend comment to " + file.getName());
        }
    }
}
