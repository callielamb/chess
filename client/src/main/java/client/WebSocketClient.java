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
                case ERROR -> handleError(message);
                case NOTIFICATION -> handleNotification(message);
            }
        } catch (Exception e) {
            System.out.println("WebSocket receive error:");
            e.printStackTrace();
        }
    }
    @OnClose
    public void onClose(CloseReason reason) {
        System.out.println("WebSocket closed: " + reason);
        client.handleSocketClosed();
    }

    private void handleError(String message) {
        try {
            websocket.messages.ErrorMessage error =
                    gson.fromJson(message, websocket.messages.ErrorMessage.class);
            System.out.println(error.getErrorMessage());
        } catch (Exception e) {
            System.out.println("Error inside handleError:");
            e.printStackTrace();
        }
    }

    private void handleNotification(String message) {
        try {
            websocket.messages.NotificationMessage note =
                    gson.fromJson(message, websocket.messages.NotificationMessage.class);
            System.out.println(note.getMessage());
        } catch (Exception e) {
            System.out.println("Error inside handleNotification:");
            e.printStackTrace();
        }
    }

    private void handleLoadGame(String message) {
        try {
            LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
            if (load == null) {
                System.out.println("LOAD_GAME parse failed: load is null");
                return;
            }
            if (load.getGame() == null) {
                System.out.println("LOAD_GAME parse failed: game is null");
                return;
            }
            if (load.getGame().game() == null) {
                System.out.println("LOAD_GAME parse failed: chess game is null");
                return;
            }
            client.updateGame(load.getGame());
        } catch (Exception e) {
            System.out.println("Error inside handleLoadGame:");
            e.printStackTrace();
        }
    }
}