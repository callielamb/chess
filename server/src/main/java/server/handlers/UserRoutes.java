package server.handlers;

import Service.UserService;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.LogoutResult;
import Result.RegisterResult;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class UserRoutes {

    private final Gson gson = new Gson();

    public UserRoutes(Javalin javalin, UserService userService) {

        //registers user
        javalin.post("/user", ctx -> {
            try {
                RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
                RegisterResult res = userService.register(req);
                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                RegisterResult res = new RegisterResult(null, null, "Error: bad request");
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                RegisterResult res = new RegisterResult(null, null, "Error: " + e.getMessage());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
            }
        });

        //login
        javalin.post("/session", ctx -> {
            try {
                LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
                LoginResult res = userService.login(req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                LoginResult res = new LoginResult(null, null, "Error: bad request");
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                LoginResult res = new LoginResult(null, null, "Error: " + e.getMessage());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
            }
        });

        //logouts
        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                LogoutResult res = userService.logout(authToken);
                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));
                return;

            } catch (Exception e) {
                ctx.status(500);
                LogoutResult res = new LogoutResult("Error: " + e.getMessage());
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