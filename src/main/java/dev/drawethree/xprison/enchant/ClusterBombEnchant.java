package dev.drawethree.xprison.enchant;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import com.google.gson.JsonObject;
import dev.drawethree.xprison.ClusterBombAddon;
import dev.drawethree.xprison.api.enchants.model.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ClusterBombEnchant extends XPrisonEnchantmentBase implements BlockBreakEnchant, ChanceBasedEnchant, RequiresPickaxeLevel, RefundableEnchant {

    private int requiredPickaxeLevel;
    private double chance;
    private int radius;

    public ClusterBombEnchant(File configFile) {
        super(configFile);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event, int level) {
        if (level <= 0) return;

        Location loc = event.getBlock().getLocation().add(0.5, 1.5, 0.5);
        World world = loc.getWorld();

        if (world == null) return;

        TNTPrimed tnt = spawnTNT(loc);

        world.playSound(loc, XSound.ENTITY_TNT_PRIMED.get(), 1f, 1f);


        new BukkitRunnable() {
            @Override
            public void run() {
                Location explodeAt = tnt.getLocation();
                if (!tnt.isDead()) {
                    tnt.remove();
                }

                getBlocksInRadius(explodeAt, radius).stream().filter(block -> ClusterBombAddon.getInstance().getApi().getEnchantsApi().isEnchantAllowed(block.getLocation())).forEach(block -> {
                    BlockBreakEvent event1 = new BlockBreakEvent(block,event.getPlayer());
                    ClusterBombAddon.getInstance().getApi().getEnchantsApi().ignoreBlockBreakEvent(event1);
                    Bukkit.getPluginManager().callEvent(event1);
                    block.setType(Material.AIR);
                });

                world.spawnParticle(XParticle.EXPLOSION.get(), explodeAt, 1);
                world.playSound(explodeAt, XSound.ENTITY_GENERIC_EXPLODE.get(), 1f, 1f);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("X-Prison"), 40L); // 2 seconds
    }

    private TNTPrimed spawnTNT(Location loc) {
        TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
        tnt.setFuseTicks(40);
        tnt.setYield(0);
        tnt.setIsIncendiary(false);
        tnt.setSilent(true);
        tnt.setCustomName(ChatColor.translateAlternateColorCodes('&',"&eCluster&c&lBomb"));
        tnt.setCustomNameVisible(false);
        return tnt;
    }

    private Set<Block> getBlocksInRadius(Location center, int radius) {
        Set<Block> blocks = new HashSet<>();
        World world = center.getWorld();
        if (world == null) return blocks;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int rSquared = radius * radius;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x + y * y + z * z) <= rSquared) {
                        Block block = world.getBlockAt(cx + x, cy + y, cz + z);
                        if (block.getType() != XMaterial.AIR.get() && block.getType().isSolid() && block.getType() != XMaterial.BEDROCK.get()) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }

        return blocks;
    }

    @Override
    protected void loadCustomProperties(JsonObject jsonObject) {
        this.requiredPickaxeLevel = jsonObject.has("pickaxeLevelRequired") ? jsonObject.get("pickaxeLevelRequired").getAsInt() : 0;
        this.chance = jsonObject.has("chance") ? jsonObject.get("chance").getAsDouble() : 1.0;
        this.radius = jsonObject.has("radius") ? jsonObject.get("radius").getAsInt() : 1;
    }

    @Override
    public String getAuthor() {
        return "Drawethree";
    }

    @Override
    public void unload() {
        // Clean up logic if needed
    }

    @Override
    public int getRequiredPickaxeLevel() {
        return this.requiredPickaxeLevel;
    }

    @Override
    public double getChanceToTrigger(int i) {
        return chance * i;
    }
}
