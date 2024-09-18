package de.jxnxsdev.craftNet.modules;

import com.google.gson.Gson;
import de.jxnxsdev.craftNet.CraftNet;
import de.jxnxsdev.craftNet.json.Message;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class WebSocket extends WebSocketServer {

    public WebSocket(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(org.java_websocket.WebSocket webSocket, ClientHandshake clientHandshake) {
        String authPassword = CraftNet.getAuthPassword();
        if (clientHandshake.getFieldValue("password") == null) {
            webSocket.close();
            return;
        }

        if (!clientHandshake.getFieldValue("password").equals(authPassword)) {
            webSocket.close(4001, "Invalid Password");
            return;
        }
    }

    @Override
    public void onClose(org.java_websocket.WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(org.java_websocket.WebSocket webSocket, String s) {
        System.out.println("Received Message: " + s);

        Gson gson = new Gson();

        Message message;
        try {
            message = gson.fromJson(s, Message.class);
        } catch (Exception e) {
            System.out.println("Failed to parse message: " + e.getMessage());
            return;
        }

        CraftNet.getInstance().getFixtureHandler().handleDMXMessage(message);
    }

    @Override
    public void onError(org.java_websocket.WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}
