package net.euphoriamc.rankgui;

import lombok.Getter;
import net.euphoriamc.rankgui.commands.RanksCommand;
import net.euphoriamc.rankgui.gui.GUIRanks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class RankGUI extends JavaPlugin {

    @Getter
    private static RankGUI instance;
    private final PluginManager pman;

    @Getter
    private static Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    public RankGUI() {
        instance = this;
        this.pman = getServer().getPluginManager();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initCommands();
        initListeners();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    private void registerCommand(CommandExecutor commandExecutor) {
        PluginCommand command = this.getCommand("ranks");
        if (command == null)
            return;
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor instanceof TabCompleter ? (TabCompleter) commandExecutor : null);
    }

    private void initCommands() {
        registerCommand(new RanksCommand());
    }

    private void registerListener(Listener listener) {
        pman.registerEvents(listener, instance);
    }

    private void initListeners() {
        registerListener(new GUIRanks());
    }
}
