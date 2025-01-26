package de.jxnxsdev.craftNet.commands;

import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.fixtures.GlassLight;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateGlassLight implements CommandExecutor {
    private static final String USAGE_MESSAGE =
            "Usage: /createGlassLight <Fixture Name> <Universe> <Start Channel> <Channels> " +
                    "<Corner 1 X> <Corner 1 Y> <Corner 1 Z> <Corner 2 X> <Corner 2 Y> <Corner 2 Z>";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("craftnet.createglasslight")) {
            sender.sendMessage("You do not have permission to use this command!");
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return false;
        }

        if (args.length != 9) {
            sender.sendMessage("Invalid number of arguments! " + USAGE_MESSAGE);
            return false;
        }

        try {
            int universe = Integer.parseInt(args[1]);
            int startChannel = Integer.parseInt(args[2]);
            int corner1X = Integer.parseInt(args[3]);
            int corner1Y = Integer.parseInt(args[4]);
            int corner1Z = Integer.parseInt(args[5]);
            int corner2X = Integer.parseInt(args[6]);
            int corner2Y = Integer.parseInt(args[7]);
            int corner2Z = Integer.parseInt(args[8]);

            Location corner1 = new Location(player.getWorld(), corner1X, corner1Y, corner1Z);
            Location corner2 = new Location(player.getWorld(), corner2X, corner2Y, corner2Z);

            GlassLight glassLight = new GlassLight(args[0], universe, startChannel, 1, corner1, corner2);
            CraftNet.getInstance().getFixtureHandler().addFixture(glassLight);

            sender.sendMessage("Glass Light created successfully!");
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid arguments! All coordinates and channels must be integers. " + USAGE_MESSAGE);
            return false;
        }

        return true;
    }
}
