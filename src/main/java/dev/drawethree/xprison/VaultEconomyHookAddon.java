package dev.drawethree.xprison;

import dev.drawethree.xprison.api.XPrisonAPI;
import dev.drawethree.xprison.api.addons.XPrisonAddon;
import dev.drawethree.xprison.api.addons.XPrisonAddonContext;
import dev.drawethree.xprison.currency.VaultCurrency;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Logger;

public final class VaultEconomyHookAddon implements XPrisonAddon {

    private XPrisonAPI api;
    private Logger logger;
    private Economy economy;
    private VaultCurrency vaultCurrencyHook;

    @Override
    public void onEnable(XPrisonAddonContext context) {
        this.api = context.getAPI();
        this.logger = context.getLogger();

        if (!setupEconomy()) {
            logger.warning("Vault plugin not found or missing Economy provider! Vault currency will not be supported.");
            return;
        }

        vaultCurrencyHook = new VaultCurrency(economy);
        api.getCurrencyApi().registerCurrency(vaultCurrencyHook);
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    @Override
    public void onDisable() {
        if (vaultCurrencyHook != null) {
            api.getCurrencyApi().unregisterCurrency(vaultCurrencyHook);
        }
    }
}
