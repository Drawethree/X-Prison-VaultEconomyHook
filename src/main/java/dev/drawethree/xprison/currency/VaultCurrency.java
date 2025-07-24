package dev.drawethree.xprison.currency;

import dev.drawethree.xprison.api.currency.model.XPrisonCurrency;
import dev.drawethree.xprison.api.currency.model.XPrisonCurrencyHandler;
import dev.drawethree.xprison.currency.handler.VaultCurrencyHandler;
import net.milkbowl.vault.economy.Economy;

public final class VaultCurrency implements XPrisonCurrency {

    private final Economy economy;

    public VaultCurrency(Economy economy) {
        this.economy = economy;
    }

    @Override
    public String getName() {
        return "Vault";
    }


    @Override
    public double getMaxAmount() {
        return Double.MAX_VALUE;
    }

    @Override
    public String getDisplayName() {
        return economy.currencyNamePlural();
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public String format(double v) {
        return economy.format(v);
    }

    @Override
    public XPrisonCurrencyHandler getHandler() {
        return new VaultCurrencyHandler(economy);
    }
}