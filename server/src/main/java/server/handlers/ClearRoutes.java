package server.handlers;

import Service.ClearService;
import Result.ClearResult;
import io.javalin.Javalin;

public class ClearRoutes {

    public ClearRoutes(Javalin javalin, ClearService clearService) {

        javalin.delete("/db", ctx -> {
            try {
                ClearResult res = clearService.clear();
                ctx.status(200);
                ctx.json(res);
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(new ClearResult("Error: " + e.getMessage()));
            }
        });
    }
}