package de.jxnxsdev.craftNet.fixtures;

import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.modules.Fixture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class GlassLight extends Fixture {

    private static final List<Material> GLASS_TYPES = Arrays.asList(
            Material.WHITE_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS,
            Material.LIME_STAINED_GLASS,
            Material.PINK_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS,
            Material.BROWN_STAINED_GLASS,
            Material.GREEN_STAINED_GLASS,
            Material.RED_STAINED_GLASS,
            Material.BLACK_STAINED_GLASS
    );

    public GlassLight(String name, int universe, int startChannel, int channels, Location position, Location topRight) {
        super(name, universe, startChannel, channels, position);
        String customData = topRight.getBlockX() + "," + topRight.getBlockY() + "," + topRight.getBlockZ();
        this.setCustomData(customData);
    }

    private Material getMaterialForValue(int value) {
        // Clamp value to ensure it's within range of the glass types list
        int index = Math.min(Math.max(value, 0), GLASS_TYPES.size() - 1);
        return GLASS_TYPES.get(index);
    }

    @Override
    public void handleUpdate() {
        int dmxValue = this.getValues()[0];

        String[] customData = this.getCustomData().split(",");
        int x = Integer.parseInt(customData[0]);
        int y = Integer.parseInt(customData[1]);
        int z = Integer.parseInt(customData[2]);

        Location topRight = new Location(this.getPosition().getWorld(), x, y, z);
        Material material = getMaterialForValue(dmxValue);

        new BukkitRunnable() {
            @Override
            public void run() {
                replaceBlocksWithGlass(getPosition(), topRight, material);
            }
        }.runTask(CraftNet.getInstance()); // Ensure this runs on the main thread
    }

    private void replaceBlocksWithGlass(Location bottomLeft, Location topRight, Material material) {
        int minX = Math.min(bottomLeft.getBlockX(), topRight.getBlockX());
        int maxX = Math.max(bottomLeft.getBlockX(), topRight.getBlockX());
        int minY = Math.min(bottomLeft.getBlockY(), topRight.getBlockY());
        int maxY = Math.max(bottomLeft.getBlockY(), topRight.getBlockY());
        int minZ = Math.min(bottomLeft.getBlockZ(), topRight.getBlockZ());
        int maxZ = Math.max(bottomLeft.getBlockZ(), topRight.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = bottomLeft.getWorld().getBlockAt(x, y, z);

                    // Ensure replacement even if the block is air
                    if (block.getType() == Material.AIR || block.getType() != material) {
                        block.setType(material);
                    }
                }
            }
        }
    }


    @Override
    public void deleteSelf() {
        // Optionally, add logic to clean up blocks or resources when this fixture is removed
    }
}
