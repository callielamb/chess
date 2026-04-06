package websocket;
import com.google.gson.Gson;
import dataaccess.Database;
import io.javalin.websocket.WsContext;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Database database;

    public WebSocketHandler(Database database) {
        this.database = database;
    }
    public void onConnect(WsContext session) {
        System.out.println("WebSocket connected: " + session.sessionId());
    }
    public void onClose(WsContext session) {
        connectionManager.remove(session);
        System.out.println("WebSocket closed: " + session.sessionId());
    }

    public void onMessage(WsContext session, String message) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> {
                    System.out.println("MAKE_MOVE not implemented yet");
                }
                case LEAVE -> {
                    System.out.println("LEAVE not implemented yet");
                }
                case RESIGN -> {
                    System.out.println("RESIGN not implemented yet");
                }
            }
        } catch (Exception ex) {
            System.out.println("WebSocket message error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void handleConnect(WsContext session, UserGameCommand command) {
        try {
            System.out.println("CONNECT received for game " + command.getGameID());
        } catch (Exception ex) {
            System.out.println("CONNECT error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}