package websocket;
import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    private final Map<Integer, Map<String, WsContext>> connections = new HashMap<>();
    private final Gson gson = new Gson();

    public void add(int gameID, String username, WsContext session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashMap<>());
        }
        connections.get(gameID).put(username, session);
    }

    public void remove(WsContext session) {
        for (Map<String, WsContext> gameConnections : connections.values()) {
            gameConnections.values().removeIf(value -> value.session.equals(session.session));
        }
    }
    public void broadcast(int gameID, ServerMessage message) {
        if (!connections.containsKey(gameID)) {
            return;
        }
        String json = gson.toJson(message);

        for (WsContext session : connections.get(gameID).values()) {
            if (session.session.isOpen()) {
                session.send(json);
            }
        }
    }

    public void broadcastExcept(int gameID, String excludedUsername, ServerMessage message) {
        if (!connections.containsKey(gameID)) {
            return;
        }
        String json = gson.toJson(message);

        for (Map.Entry<String, WsContext> entry : connections.get(gameID).entrySet()) {
            if (!entry.getKey().equals(excludedUsername) && entry.getValue().session.isOpen()) {
                entry.getValue().send(json);
            }
        }
    }
}