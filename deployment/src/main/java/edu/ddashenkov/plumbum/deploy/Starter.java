package edu.ddashenkov.plumbum.deploy;

import edu.ddashenkov.plumbum.deploy.endpoint.Endpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.LoginEndpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.RecordEndpoint;

import java.util.stream.Stream;

import static spark.Spark.before;
import static spark.Spark.options;

final class Starter {

    /**
     * The {@code private} constructor prevents the utility class instantiation.
     */
    private Starter() {}


    public static void main(String[] args) {
        enableCORS();
        Stream.of(LoginEndpoint.create(),
                  RecordEndpoint.create())
              .forEach(Endpoint::serve);
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
