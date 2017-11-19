package edu.ddashenkov.plumbum.webadapter;

import static spark.Spark.get;
import static spark.Spark.patch;
import static spark.Spark.put;

final class RecordController implements Controller {

    private RecordController() {
        // Prevent direct instantiation.
    }

    static Controller create() {
        return new RecordController();
    }

    @Override
    public void serve() {
        get("/records", (request, response) -> null);
        get("/record/:id", (request, response) -> null);

        put("/record/:id", (request, response) -> null);
        patch("/record/:id", (request, response) -> null);
    }
}
