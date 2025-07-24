package dev.drawethree.xprison.currency.handler;

import dev.drawethree.xprison.api.currency.enums.LostCause;
import dev.drawethree.xprison.api.currency.enums.ReceiveCause;
import dev.drawethree.xprison.api.currency.model.XPrisonCurrencyHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public final class VaultCurrencyHandler implements XPrisonCurrencyHandler {

    private final Economy economy;

    public VaultCurrencyHandler(Economy economy) {
        this.economy = economy;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        return economy.getBalance(offlinePlayer);
    }

    @Override
    public boolean setBalance(OfflinePlayer offlinePlayer, double v) {
        double balance = economy.getBalance(offlinePlayer);
        if (balance > 0) {
            return economy.withdrawPlayer(offlinePlayer, balance).transactionSuccess();
        }
        return true;
    }

    @Override
    public boolean addBalance(OfflinePlayer offlinePlayer, double v, ReceiveCause receiveCause) {
        return economy.depositPlayer(offlinePlayer,v).transactionSuccess();
    }

    @Override
    public boolean removeBalance(OfflinePlayer offlinePlayer, double v, LostCause lostCause) {
        return economy.withdrawPlayer(offlinePlayer,v).transactionSuccess();
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return economy.has(offlinePlayer,v);
    }
}