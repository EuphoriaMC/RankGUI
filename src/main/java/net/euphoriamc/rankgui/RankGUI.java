package net.euphoriamc.rankgui;

import lombok.Getter;
import net.euphoriamc.rankgui.commands.RanksCommand;
import net.euphoriamc.rankgui.gui.GUIRanks;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RankGUI extends JavaPlugin {

    @Getter
    private static RankGUI instance;
    private final PluginManager pman;

    public RankGUI() {
        instance = this;
        this.pman = getServer().getPluginManager();
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        initCommands();
        initListeners();
    }

    private void registerCommand(String commandName, CommandExecutor commandExecutor) {
        PluginCommand command = this.getCommand(commandName);
        if (command == null)
            return;
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor instanceof TabCompleter ? (TabCompleter) commandExecutor : null);
    }

    private void initCommands() {
        registerCommand("ranks", new RanksCommand());
    }

    private void registerListener(Listener listener) {
        pman.registerEvents(listener, instance);
    }

    private void initListeners() {
        registerListener(new GUIRanks());
    }
}
