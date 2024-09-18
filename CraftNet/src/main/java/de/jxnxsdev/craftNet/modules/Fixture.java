package de.jxnxsdev.craftNet.modules;

import de.jxnxsdev.craftNet.json.Channel;
import de.jxnxsdev.craftNet.json.Universe;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public abstract class Fixture {
    @Getter @Setter
    String name;
    @Getter @Setter
    UUID id;
    @Getter @Setter
    int universe;
    @Getter @Setter
    int startChannel;
    @Getter @Setter
    int channels;
    @Getter @Setter
    int[] values;
    int[] lastValues;


    public Fixture(String name, int universe, int startChannel, int channels) {
        this.name = name;
        this.universe = universe;
        this.startChannel = startChannel;
        this.channels = channels;
        this.values = new int[channels];
        this.lastValues = new int[channels];
        this.id = UUID.randomUUID();
    }

    public void updateValues(Universe universe) {
        for (int i = 0; i < channels; i++) {
            Channel channel = universe.getChannels().get(i);
            values[i] = channel.getValue();
        }

        if (values != lastValues) {
            handleUpdate();
        }
    }

    public abstract void handleUpdate();
    public abstract void deleteSelf();
}
