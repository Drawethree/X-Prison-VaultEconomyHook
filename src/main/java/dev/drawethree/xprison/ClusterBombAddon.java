package dev.drawethree.xprison;

import dev.drawethree.xprison.api.XPrisonAPI;
import dev.drawethree.xprison.api.addons.XPrisonAddon;
import dev.drawethree.xprison.enchant.ClusterBombEnchant;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class ClusterBombAddon implements XPrisonAddon {

    private static ClusterBombAddon instance;
    private XPrisonAPI api;
    private ClusterBombEnchant enchant;
    private File dataFolder;

    public static ClusterBombAddon getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        api = XPrisonAPI.getInstance();

        this.dataFolder = new File(Bukkit.getPluginManager().getPlugin("X-Prison").getDataFolder(), "addons-data/ClusterBomb");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        copyDefaultEnchantConfiguration();

        File configFile = new File(dataFolder, "clusterbomb.json");
        this.enchant = new ClusterBombEnchant(configFile);
        this.enchant.load();

        this.api.getEnchantsApi().registerEnchant(this.enchant);

    }

    @Override
    public void onDisable() {
        if (this.enchant != null) {
            this.enchant.unload();
            this.api.getEnchantsApi().unregisterEnchant(this.enchant);
        }
    }

    private void copyDefaultEnchantConfiguration() {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File outFile = new File(dataFolder, "clusterbomb.json");

        if (!outFile.exists()) {
            try (InputStream in = ClusterBombAddon.class.getResourceAsStream("/clusterbomb.json")) {
                if (in != null) {
                    Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Bukkit.getLogger().info("[ClusterBombEnchant] Copied clusterbomb.json");
                } else {
                    Bukkit.getLogger().warning("[ClusterBombEnchant] Could not find clusterbomb.json in resources!");
                }
            } catch (IOException e) {
                Bukkit.getLogger().warning("[ClusterBombEnchant] Failed to copy clusterbomb.json: " + e.getMessage());
            }
        }
    }

    public XPrisonAPI getApi() {
        return api;
    }
}
