package client;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;

import jakarta.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {

    private final Gson gson = new Gson();
    private Session session;
    private final ChessClient client;

    public WebSocketClient(ChessClient client, String url) {
        try {
            this.client = client;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, URI.create(url));
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect websocket");
        }
    }

    public void send(String message) {
        session.getAsyncRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> handleLoadGame(message);
                case ERROR -> System.out.println("Error received");
                case NOTIFICATION -> System.out.println("Notification received");
            }

        } catch (Exception e) {
            System.out.println("WebSocket receive error");
        }
    }

    private void handleLoadGame(String message) {
        LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
        client.updateGame(load.getGame());
    }
}