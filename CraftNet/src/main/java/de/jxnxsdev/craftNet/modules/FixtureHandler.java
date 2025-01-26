package de.jxnxsdev.craftNet.modules;

import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.fixtures.GlassLight;
import de.jxnxsdev.craftNet.json.Message;
import de.jxnxsdev.craftNet.json.Universe;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class FixtureHandler {
    private Map<UUID, Fixture> fixtures = new HashMap<>();

    public FixtureHandler() {
        loadFixtures();
    }

    public void handleDMXMessage(Message message) {
        if (fixtures.isEmpty()) {
            return;
        }

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
        System.out.println("Adding fixture " + fixture.getName());
        fixtures.put(fixture.getId(), fixture);
        saveFixtures();
    }

    public void removeFixture(Fixture fixture) {
        fixture.deleteSelf();
        fixtures.remove(fixture.getId());
        saveFixtures();
    }

    public void removeFixture(UUID id) {
        fixtures.get(id).deleteSelf();
        fixtures.remove(id);
        saveFixtures();
    }


    private void loadFixtures() {
        String filepath = CraftNet.getInstance().getDataFolder().getAbsolutePath() + "/fixtures.xml";
        File file = new File(filepath);

        if (!file.exists()) {
            System.out.println("No fixtures.xml file found. Skipping loading.");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Fixture");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String name = element.getElementsByTagName("Name").item(0).getTextContent();
                    UUID id = UUID.fromString(element.getElementsByTagName("ID").item(0).getTextContent());
                    int universe = Integer.parseInt(element.getElementsByTagName("Universe").item(0).getTextContent());
                    int startChannel = Integer.parseInt(element.getElementsByTagName("StartChannel").item(0).getTextContent());
                    int channels = Integer.parseInt(element.getElementsByTagName("Channels").item(0).getTextContent());
                    String worldName = element.getElementsByTagName("World").item(0).getTextContent();
                    double x = Double.parseDouble(element.getElementsByTagName("X").item(0).getTextContent());
                    double y = Double.parseDouble(element.getElementsByTagName("Y").item(0).getTextContent());
                    double z = Double.parseDouble(element.getElementsByTagName("Z").item(0).getTextContent());
                    String customData = element.getElementsByTagName("CustomData").item(0).getTextContent();

                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        System.out.println("World " + worldName + " not found. Skipping fixture " + name);
                        continue;
                    }

                    Location position = new Location(world, x, y, z);

                    Fixture fixture = new GlassLight(name, universe, startChannel, channels, position, position); // Replace with correct class if needed
                    fixture.setId(id);
                    fixture.setCustomData(customData);

                    fixtures.put(id, fixture);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFixtures() {
        String filepath = CraftNet.getInstance().getDataFolder().getAbsolutePath() + "/fixtures.xml";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("Fixtures");
            doc.appendChild(root);

            for (Fixture fixture : fixtures.values()) {
                Element fixtureElement = doc.createElement("Fixture");

                Element nameElement = doc.createElement("Name");
                nameElement.appendChild(doc.createTextNode(fixture.getName()));
                fixtureElement.appendChild(nameElement);

                Element idElement = doc.createElement("ID");
                idElement.appendChild(doc.createTextNode(fixture.getId().toString()));
                fixtureElement.appendChild(idElement);

                Element universeElement = doc.createElement("Universe");
                universeElement.appendChild(doc.createTextNode(String.valueOf(fixture.getUniverse())));
                fixtureElement.appendChild(universeElement);

                Element startChannelElement = doc.createElement("StartChannel");
                startChannelElement.appendChild(doc.createTextNode(String.valueOf(fixture.getStartChannel())));
                fixtureElement.appendChild(startChannelElement);

                Element channelsElement = doc.createElement("Channels");
                channelsElement.appendChild(doc.createTextNode(String.valueOf(fixture.getChannels())));
                fixtureElement.appendChild(channelsElement);

                Location position = fixture.getPosition();
                Element worldElement = doc.createElement("World");
                worldElement.appendChild(doc.createTextNode(position.getWorld().getName()));
                fixtureElement.appendChild(worldElement);

                Element xElement = doc.createElement("X");
                xElement.appendChild(doc.createTextNode(String.valueOf(position.getX())));
                fixtureElement.appendChild(xElement);

                Element yElement = doc.createElement("Y");
                yElement.appendChild(doc.createTextNode(String.valueOf(position.getY())));
                fixtureElement.appendChild(yElement);

                Element zElement = doc.createElement("Z");
                zElement.appendChild(doc.createTextNode(String.valueOf(position.getZ())));
                fixtureElement.appendChild(zElement);

                Element customDataElement = doc.createElement("CustomData");
                customDataElement.appendChild(doc.createTextNode(fixture.getCustomData()));
                fixtureElement.appendChild(customDataElement);

                root.appendChild(fixtureElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileWriter(filepath));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
