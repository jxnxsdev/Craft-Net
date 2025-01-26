package de.jxnxsdev.craftNet.commands;

import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.modules.Fixture;
import de.jxnxsdev.craftNet.modules.FixtureHandler;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DeleteFixture implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // check if the player has the permission to delete a fixture
        if (!commandSender.hasPermission("craftnet.deletefixture")) {
            commandSender.sendMessage("You do not have permission to use this command!");
            return false;
        }

        // check if the player is a player
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("You must be a player to use this command!");
            return false;
        }

        // check if theres 3 arguments (x, y, z)
        if (strings.length != 3) {
            commandSender.sendMessage("Invalid number of arguments! Usage: /deleteFixture <X> <Y> <Z>");
            return false;
        }

        // get the x, y, z coordinates
        try {
            int x = Integer.parseInt(strings[0]);
            int y = Integer.parseInt(strings[1]);
            int z = Integer.parseInt(strings[2]);

            World world = player.getWorld();

            Location location = new Location(world, x, y, z);

            CraftNet.getInstance().getFixtureHandler().removeFixture(location);

            commandSender.sendMessage("Fixture deleted successfully!");
            return true;
        } catch (NumberFormatException e) {
            commandSender.sendMessage("Invalid arguments! All coordinates must be integers. Usage: /deleteFixture <X> <Y> <Z>");
            return false;
        }
    }
}
