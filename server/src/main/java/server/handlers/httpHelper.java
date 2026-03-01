package server.handlers;

import io.javalin.http.Context;

public class httpHelper {

    public static void setStatus(Context ctx, String message) {
        if (message == null) {
            ctx.status(200);
            return;
        }
        if (message.equals("Error:bad request")) {
            ctx.status(400);
            return;
        }
        if (message.equals("Error:unauthorized")) {
            ctx.status(401);
            return;
        }

        if (message.equals("Error: already taken ")) {
            ctx.status(403);
            return;
        }

        ctx.status(500);
    }
}