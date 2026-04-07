package websocket;
import com.google.gson.Gson;
import dataaccess.Database;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

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
                case CONNECT -> {
                    websocket.commands.ConnectCommand connectCommand =
                            gson.fromJson(message, websocket.commands.ConnectCommand.class);
                    handleConnect(session, connectCommand);
                }
                case MAKE_MOVE -> System.out.println("MAKE_MOVE not implemented yet");
                case LEAVE -> {
                    websocket.commands.LeaveCommand leaveCommand =
                            gson.fromJson(message, websocket.commands.LeaveCommand.class);
                    handleLeave(session, leaveCommand);
                }
                case RESIGN -> System.out.println("RESIGN not implemented yet");
            }
        } catch (Exception ex) {
            System.out.println("WebSocket message error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    private void handleConnect(WsContext session, websocket.commands.ConnectCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();

            AuthData auth = database.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }
            String username = auth.username();

            GameData game = database.getGame(gameID);
            if (game == null) {
                sendError(session, "Error: game not found");
                return;
            }
            connectionManager.add(gameID, username, session);

            LoadGameMessage loadMessage = new LoadGameMessage(game);
            session.send(gson.toJson(loadMessage));

            String role;
            String requestedColor = command.getPlayerColor();

            if (requestedColor == null) {
                role = "OBSERVER";
            } else {
                role = requestedColor.toUpperCase();
            }

            String joinMessage;
            if (role.equals("OBSERVER")) {
                joinMessage = username + " joined the game as an observer.";
            } else {
                joinMessage = username + " joined the game as " + role + ".";
            }

            connectionManager.broadcastExcept(
                    gameID,
                    username,
                    new NotificationMessage(joinMessage)
            );

            System.out.println(username + " connected to game " + gameID + " as " + role);

        } catch (Exception ex) {
            sendError(session, ex.getMessage());
        }
    }
    private void handleLeave(WsContext session, websocket.commands.LeaveCommand command) {
        try {
            String authToken = command.getAuthToken();
            int gameID = command.getGameID();

            AuthData auth = database.getAuth(authToken);
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }
            String username = auth.username();
            connectionManager.remove(session);
            connectionManager.broadcast(
                    gameID,
                    new NotificationMessage(username + " left the game.")
            );
            System.out.println(username + " left game " + gameID);
        } catch (Exception ex) {
            sendError(session, ex.getMessage());
        }
    }
    private void sendError(WsContext session, String errorMessage) {
        try {
            ErrorMessage error = new ErrorMessage(errorMessage);
            session.send(gson.toJson(error));
        } catch (Exception ignored) {}
    }
}