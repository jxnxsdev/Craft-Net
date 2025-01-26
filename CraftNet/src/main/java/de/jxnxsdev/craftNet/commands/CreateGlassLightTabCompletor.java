package de.jxnxsdev.craftNet.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateGlassLightTabCompletor implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("CraftNet.createGlassLight")) {
            return Collections.emptyList();
        }

        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        List<String> suggestions = new ArrayList<>();

        switch (args.length) {
            case 1 -> suggestions.add("<Fixture Name>"); // Suggest placeholder for fixture name (string)
            case 2 -> suggestions.add("<Universe>"); // Suggest placeholder for universe (integer)
            case 3 -> suggestions.add("<Start Channel>"); // Suggest placeholder for start channel (integer)
            case 4, 7 -> suggestions.add(player.getTargetBlock(null, 10).getX() + "");
            case 5, 8 -> suggestions.add(player.getTargetBlock(null, 10).getY() + "");
            case 6, 9 -> suggestions.add(player.getTargetBlock(null, 10).getZ() + "");

            default -> suggestions = Collections.emptyList(); // No suggestions for other arguments
        }

        // Return suggestions that start with the current input
        String currentInput = args[args.length - 1].toLowerCase();
        return suggestions.stream()
                .filter(suggestion -> suggestion.toLowerCase().startsWith(currentInput))
                .toList();
    }
}
