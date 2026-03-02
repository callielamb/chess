package server.handlers;

import service.GameService;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class GameRoutes {

    private final Gson gson = new Gson();

    public GameRoutes(Javalin javalin, GameService gameService) {

        //list availbale games
        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                ListGamesResult res = gameService.listGames(authToken);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                ListGamesResult res = new ListGamesResult(null, "Error: " + e.getMessage());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
            }
        });

        //creates game
        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
                CreateGameResult res = gameService.createGame(authToken, req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                CreateGameResult res = new CreateGameResult(null, "Error: bad request");
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                CreateGameResult res = new CreateGameResult(null, "Error: " + e.getMessage());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
            }
        });

        //join a game
        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
                JoinGameResult res = gameService.joinGame(authToken, req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                JoinGameResult res = new JoinGameResult("Error: bad request");
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                JoinGameResult res = new JoinGameResult("Error: " + e.getMessage());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
            }
        });
    }

    private void setStatus(Context ctx, String message) {

        if (message == null) {
            ctx.status(200);
            return;
        }
        if (message.equals("Error: bad request")) {
            ctx.status(400);
            return;
        }
        if (message.equals("Error: unauthorized")) {
            ctx.status(401);
            return;
        }
        if (message.equals("Error: already taken")) {
            ctx.status(403);
            return;
        }
        ctx.status(500);
    }
}