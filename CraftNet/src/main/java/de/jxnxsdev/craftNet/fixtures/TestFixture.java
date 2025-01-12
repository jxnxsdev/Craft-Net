package de.jxnxsdev.craftNet.fixtures;

import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.modules.Fixture;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class TestFixture extends Fixture {

    private final int MAX_BLOCKS = 4;

    public TestFixture(String name, int universe, int startChannel, int channels) {
        super(name, universe, startChannel, channels);
    }

    @Override
    public void handleUpdate() {
        // log a color of the rgb value (values[0], values[1], values[2]) with the brightness values[3] to the console
        int r = this.getValues()[0];
        int g = this.getValues()[1];
        int b = this.getValues()[2];
        int w = this.getValues()[3];

        System.out.println("Color: " + r + ", " + g + ", " + b + " with white: " + w);

        // at position 0 100 0 in the world is a beacon. Create code to place up to 5 colored glass blocks above the beacon to make the color of it match the rgb value
        setBeaconColor(r, g, b, w);
    }

    // Function to mix the glass blocks above the beacon based on RGBW values
    private void setBeaconColor(int r, int g, int b, int w) {
        // Schedule the block changes on the main thread
        new BukkitRunnable() {
            @Override
            public void run() {
                // Get the world
                World world = Bukkit.getWorld("world");  // Assuming the world name is "world"

                if (world == null) {
                    return; // World not found
                }

                // Define the beacon location (0, 100, 0)
                Location beaconLocation = new Location(world, 0, 100, 0);

                // Place up to 5 glass blocks above the beacon, starting from Y = 101
                int startY = 101;

                // We'll use up to 5 blocks: 1 each for Red, Green, Blue, White, and one for blending if needed
                Material[] glassMaterials = calculateGlassMaterials(r, g, b, w);

                for (int i = 0; i < glassMaterials.length; i++) {
                    Location glassLocation = new Location(world, beaconLocation.getX(), startY + i, beaconLocation.getZ());
                    Block glassBlock = world.getBlockAt(glassLocation);

                    // Set the block type to the specific colored stained glass
                    glassBlock.setType(glassMaterials[i]);
                }
            }
        }.runTask(CraftNet.getInstance()); // Ensures this runs on the main server thread
    }

    // Function to calculate the Material values for RGBW using up to 5 blocks
    private Material[] calculateGlassMaterials(int r, int g, int b, int w) {
        // Normalize RGB values to fit the use of 5 blocks for blending
        Material[] glassMaterials = new Material[5];

        // Divide the total intensity of the color among the 5 blocks
        int totalIntensity = r + g + b + w;

        if (totalIntensity == 0) {
            // If all colors are zero, default to WHITE glass
            return new Material[]{Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS};
        }

        // Calculate the proportion of each color
        double redProportion = (double) r / totalIntensity;
        double greenProportion = (double) g / totalIntensity;
        double blueProportion = (double) b / totalIntensity;
        double whiteProportion = (double) w / totalIntensity;

        // Assign block colors proportionally, blending using red, green, blue, and white glass
        int redBlocks = (int) Math.round(redProportion * 5);
        int greenBlocks = (int) Math.round(greenProportion * 5);
        int blueBlocks = (int) Math.round(blueProportion * 5);
        int whiteBlocks = (int) Math.round(whiteProportion * 5);

        // Fill up the glassMaterials array
        int index = 0;
        for (int i = 0; i < redBlocks && index < 5; i++) {
            glassMaterials[index++] = Material.RED_STAINED_GLASS;
        }
        for (int i = 0; i < greenBlocks && index < 5; i++) {
            glassMaterials[index++] = Material.GREEN_STAINED_GLASS;
        }
        for (int i = 0; i < blueBlocks && index < 5; i++) {
            glassMaterials[index++] = Material.BLUE_STAINED_GLASS;
        }
        for (int i = 0; i < whiteBlocks && index < 5; i++) {
            glassMaterials[index++] = Material.WHITE_STAINED_GLASS;
        }

        // Fill remaining blocks with default white if not all 5 blocks are used
        while (index < 5) {
            glassMaterials[index++] = Material.WHITE_STAINED_GLASS;
        }

        return glassMaterials;
    }

    @Override
    public void deleteSelf() {

    }
}
