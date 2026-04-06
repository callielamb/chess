package websocket;
import io.javalin.websocket.WsContext;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private final Map<String, WsContext> connections = new HashMap<>();

    public void add(String visitorName, WsContext session) {
        connections.put(visitorName, session);
    }
    public void remove(WsContext session) {
        connections.values().removeIf(value -> value.session.equals(session.session));
    }
}