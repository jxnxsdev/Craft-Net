package de.jxnxsdev.craftNet.modules;

import de.jxnxsdev.craftNet.json.Channel;
import de.jxnxsdev.craftNet.json.Universe;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

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
    Location position;
    @Getter @Setter
    String customData;
    @Getter @Setter
    int[] values;
    int[] lastValues;


    public Fixture(String name, int universe, int startChannel, int channels, Location position) {
        this.name = name;
        this.universe = universe;
        this.startChannel = startChannel;
        this.channels = channels;
        this.position = position;
        this.values = new int[channels];
        this.lastValues = new int[channels];
        this.id = UUID.randomUUID();
    }

    public void updateValues(Universe universe) {

        for (int i = 0; i < channels; i++) {
            if (i + startChannel >= universe.getChannels().size()) {
                break;
            }
            Channel channel = universe.getChannels().get(i + startChannel);
            values[i] = channel.getValue();
        }

        boolean changed = false;
        for (int i = 0; i < channels; i++) {
            if (values[i] != lastValues[i]) {
                changed = true;
                break;
            }
        }

        if (changed) {
            handleUpdate();
            System.arraycopy(values, 0, lastValues, 0, channels);
        }
    }

    public abstract void handleUpdate();
    public abstract void deleteSelf();
}
