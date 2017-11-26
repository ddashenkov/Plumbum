package edu.ddashenkov.plumbum.webadapter;

import java.util.stream.Stream;

import static spark.Spark.before;
import static spark.Spark.options;

final class Starter {

    private Starter() {
        // Prevent utility class instantiation.
    }

    public static void main(String[] args) {
        enableCORS();
        Stream.of(LoginController.create(),
                  RecordController.create())
              .forEach(Controller::serve);
    }

    private static void enableCORS() {
        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            final String frontend = "http://localhost:8000";
            response.header("Access-Control-Allow-Origin", frontend);
            response.header("Access-Control-Request-Method", frontend);
            response.header("Access-Control-Allow-Headers", frontend);
            response.header("Access-Control-Allow-Credentials", "true");
            response.type("application/json");
        });
    }
}
