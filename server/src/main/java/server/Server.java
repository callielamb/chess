package server;
import dataaccess.DatabaseManager;
import service.ClearService;
import service.GameService;
import service.UserService;
import dataaccess.Database;
import dataaccess.InsertData;
import io.javalin.Javalin;
import server.handlers.ClearRoutes;
import server.handlers.GameRoutes;
import server.handlers.UserRoutes;

public class Server {

    private final Javalin javalin;
    private final Database database = new InsertData();
    private final ClearService clearService = new ClearService(database);
    private final UserService userService = new UserService(database);
    private final GameService gameService = new GameService(database);

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        new ClearRoutes(javalin, clearService);
        new UserRoutes(javalin, userService);
        new GameRoutes(javalin, gameService);
    }

    public int run(int desiredPort) {
        try {
            DatabaseManager.createDatabase();
            DatabaseManager.createTables();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}