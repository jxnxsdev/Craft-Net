package de.jxnxsdev.craftNet.fixtures;

import de.jxnxsdev.craftNet.modules.Fixture;

public class TestFixture extends Fixture {

    public TestFixture(String name, int universe, int startChannel, int channels) {
        super(name, universe, startChannel, channels);
    }

    @Override
    public void handleUpdate() {
        // log a color of the rgb value (values[0], values[1], values[2]) with the brightness values[3] to the console
        int r = this.getValues()[0];
        int g = this.getValues()[1];
        int b = this.getValues()[2];
        int brightness = this.getValues()[3];

        System.out.println("Color: " + r + ", " + g + ", " + b + " with brightness " + brightness);
    }

    @Override
    public void deleteSelf() {

    }
}
