package server.handlers;
import Service.GameService;
import Request.CreateGameRequest;
import Request.JoinGameRequest;
import Result.CreateGameResult;
import Result.JoinGameResult;
import Result.ListGamesResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import com.google.gson.JsonSyntaxException;

public class GameRoutes {

    private final Gson gson = new Gson();

    public GameRoutes(Javalin javalin, GameService gameService) {

        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                ListGamesResult res = gameService.listGames(authToken);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                ctx.json(new CreateGameResult(null, "Error: bad request"));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new ListGamesResult(null, "Error: " + e.getMessage()));
            }
        });

        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
                CreateGameResult res = gameService.createGame(authToken, req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                ctx.json(new CreateGameResult(null, "Error: bad request"));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new CreateGameResult(null, "Error: " + e.getMessage()));
            }
        });

        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
                JoinGameResult res = gameService.joinGame(authToken, req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new JoinGameResult("Error: " + e.getMessage()));
            }
        });
    }

    private void setStatus(io.javalin.http.Context ctx, String message) {
        if (message == null) {
            ctx.status(200);
        } else if (message.equals("Error:bad request")) {
            ctx.status(400);
        } else if (message.equals("Error: unauthorized")) {
            ctx.status(401);
        } else if (message.equals("Error: already taken ")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }
    }
}