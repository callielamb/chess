package server;

import dataaccess.SqlDataAccess;
import service.ClearService;
import service.GameService;
import service.UserService;
import dataaccess.Database;
import io.javalin.Javalin;
import server.handlers.ClearRoutes;
import server.handlers.GameRoutes;
import server.handlers.UserRoutes;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;
    private final Database database = new SqlDataAccess();
    private final ClearService clearService = new ClearService(database);
    private final UserService userService = new UserService(database);
    private final GameService gameService = new GameService(database);
    private final WebSocketHandler webSocketHandler = new WebSocketHandler(database);

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        new ClearRoutes(javalin, clearService);
        new UserRoutes(javalin, userService);
        new GameRoutes(javalin, gameService);

        javalin.ws("/ws", ws -> {
            ws.onConnect(ctx -> {ctx.enableAutomaticPings(); webSocketHandler.onConnect(ctx);});
            ws.onClose(webSocketHandler::onClose);
            ws.onMessage(ctx -> webSocketHandler.onMessage(ctx, ctx.message()));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }
    public void stop() {
        javalin.stop();
    }
}