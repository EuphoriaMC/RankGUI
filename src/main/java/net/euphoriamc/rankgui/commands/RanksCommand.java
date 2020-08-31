package net.euphoriamc.rankgui.commands;

import net.euphoriamc.rankgui.gui.GUIRanks;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RanksCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("ranks.reload")) {
                GUIRanks.reload();
                sender.sendMessage(ChatColor.RED + "Ranks config has been reloaded.");
                return true;
            }
        }

        if (sender instanceof ConsoleCommandSender)
            return true;

        new GUIRanks((Player) sender).openInventory();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
