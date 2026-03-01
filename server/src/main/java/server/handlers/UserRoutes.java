package server.handlers;
import Service.UserService;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.LogoutResult;
import Result.RegisterResult;
import com.google.gson.Gson;
import io.javalin.Javalin;
import com.google.gson.JsonSyntaxException;

public class UserRoutes {

    private final Gson gson = new Gson();

    public UserRoutes(Javalin javalin, UserService userService) {

        javalin.post("/user", ctx -> {
            try {
                RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
                RegisterResult res = userService.register(req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                ctx.json(new RegisterResult(null, null, "Error: bad request"));

            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new RegisterResult(null, null, "Error: " + e.getMessage()));
            }
        });

        javalin.post("/session", ctx -> {
            try {
                LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
                LoginResult res = userService.login(req);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (JsonSyntaxException e) {
                ctx.status(400);
                ctx.json(new RegisterResult(null, null, "Error: bad request"));

            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new LoginResult(null, null, "Error: " + e.getMessage()));
            }
        });

        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                LogoutResult res = userService.logout(authToken);

                setStatus(ctx, res.message());
                ctx.contentType("application/json");
                ctx.result(gson.toJson(res));

            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new LogoutResult("Error: " + e.getMessage()));
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