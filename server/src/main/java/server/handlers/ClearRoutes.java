package server.handlers;

import service.ClearService;
import result.ClearResult;
import io.javalin.Javalin;

public class ClearRoutes {

    public ClearRoutes(Javalin javalin, ClearService clearService) {

        javalin.delete("/db", ctx -> {
            try {
                ClearResult res = clearService.clear();
                ctx.status(200);
                ctx.contentType("application/json");
                ctx.result("{}");
                return;
            } catch (Exception e) {
                ctx.status(500);
                ctx.contentType("application/json");
                ctx.result("{\"message\":\"Error: " + e.getMessage() + "\"}");
            }
        });
    }
}