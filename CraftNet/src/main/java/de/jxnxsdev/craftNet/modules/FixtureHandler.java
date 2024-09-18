package de.jxnxsdev.craftNet.modules;

import de.jxnxsdev.craftNet.fixtures.TestFixture;
import de.jxnxsdev.craftNet.json.Message;
import de.jxnxsdev.craftNet.json.Universe;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FixtureHandler {
    private Map<UUID, Fixture> fixtures;

    public FixtureHandler() {

    }

    public void handleDMXMessage(Message message) {
        List<Universe> universes = message.getData();
        for (Universe universe : universes) {
            for (Fixture fixture : fixtures.values()) {
                if (fixture.getUniverse() == universe.getId()) {
                    fixture.updateValues(universe);
                }
            }
        }
    }

    public void addFixture(Fixture fixture) {
        fixtures.put(fixture.getId(), fixture);
    }

    public void removeFixture(Fixture fixture) {
        fixture.deleteSelf();
        fixtures.remove(fixture.getId());
    }

    public void removeFixture(UUID id) {
        fixtures.get(id).deleteSelf();
        fixtures.remove(id);
    }

    private void loadFixtures() {
        TestFixture testFixture = new TestFixture("Test Fixture", 0, 0, 4);
        addFixture(testFixture);


        // TODO: Load this from a file
    }
}
