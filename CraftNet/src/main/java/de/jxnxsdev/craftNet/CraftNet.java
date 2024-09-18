package de.jxnxsdev.craftNet;

import de.jxnxsdev.craftNet.modules.FixtureHandler;
import de.jxnxsdev.craftNet.modules.WebSocket;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.UnknownHostException;

public final class CraftNet extends JavaPlugin {
    @Getter
    private static CraftNet instance;

    @Getter
    private static String authPassword;

    @Getter
    private final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder().resolver(StandardTags.defaults()).build())
            .build();

    @Getter
    private WebSocket webSocket;

    @Getter
    public FixtureHandler fixtureHandler;

    @Override
    public void onEnable() {
        initializePlugin();
        initializeWebSocket();
    }

    private void initializePlugin() {
        saveDefaultConfig();
        reloadConfig();
        instance = this;
        authPassword = getConfig().getString("socket.password");
        fixtureHandler = new FixtureHandler();
    }

    private void initializeWebSocket() {
        int port = getConfig().getInt("socket.port");
        try {
            webSocket = new WebSocket(port);
            webSocket.start();
            getLogger().info("WebSocket Server Opened on port " + port + "!");
        } catch (UnknownHostException e) {
            getLogger().severe("Failed to start WebSocket: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        if (webSocket != null) {
            try {
                webSocket.stop();
                getLogger().info("WebSocket Server Closed.");
            } catch (IOException | InterruptedException e) {
                getLogger().severe("Error occurred while stopping WebSocket: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
