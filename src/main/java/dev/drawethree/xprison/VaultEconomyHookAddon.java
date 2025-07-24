package dev.drawethree.xprison;

import dev.drawethree.xprison.api.XPrisonAPI;
import dev.drawethree.xprison.api.addons.XPrisonAddon;
import dev.drawethree.xprison.currency.VaultCurrency;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public final class VaultEconomyHookAddon implements XPrisonAddon, Listener {

    private static VaultEconomyHookAddon instance;
    private XPrisonAPI api;
    private Economy economy;
    private VaultCurrency vaultCurrencyHook;

    @Override
    public void onEnable() {
        instance = this;
        api = XPrisonAPI.getInstance();


        if(setupEconomy()) {
            vaultCurrencyHook = new VaultCurrency(economy);
            api.getCurrencyApi().registerCurrency(vaultCurrencyHook);
        }
    }

    private boolean setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

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

    public XPrisonAPI getApi() {
        return api;
    }
}
