package websocket;
import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {

    private final Map<Integer, Map<String, Connection>> connections = new HashMap<>();
    private final Gson gson = new Gson();

    private static class Connection {
        private final String username;
        private final String role;
        private final WsContext session;

        public Connection(String username, String role, WsContext session) {
            this.username = username;
            this.role = role;
            this.session = session;
        }
    }

    public void add(int gameID, String username, String role, WsContext session) {
        if (!connections.containsKey(gameID)) {
            connections.put(gameID, new HashMap<>());
        }

        connections.get(gameID).put(username, new Connection(username, role, session));
    }

    public void remove(WsContext session) {
        for (Map<String, Connection> gameConnections : connections.values()) {
            gameConnections.values().removeIf(value -> value.session.session.equals(session.session));
        }
    }
    public void broadcast(int gameID, ServerMessage message) {
        if (!connections.containsKey(gameID)) {
            return;
        }
        String json = gson.toJson(message);

        for (Connection connection : connections.get(gameID).values()) {
            if (connection.session.session.isOpen()) {
                connection.session.send(json);
            }
        }
    }

    public void broadcastExcept(int gameID, String excludedUsername, ServerMessage message) {
        if (!connections.containsKey(gameID)) {
            return;
        }
        String json = gson.toJson(message);

        for (Map.Entry<String, Connection> entry : connections.get(gameID).entrySet()) {
            if (!entry.getKey().equals(excludedUsername) && entry.getValue().session.session.isOpen()) {
                entry.getValue().session.send(json);
            }
        }
    }
}