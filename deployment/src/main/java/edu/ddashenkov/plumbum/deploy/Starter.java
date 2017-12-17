package edu.ddashenkov.plumbum.deploy;

import edu.ddashenkov.plumbum.deploy.endpoint.Endpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.LoginEndpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.RecordEndpoint;

import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static spark.Spark.before;
import static spark.Spark.port;

final class Starter {

    /**
     * The {@code private} constructor prevents the utility class instantiation.
     */
    private Starter() {
    }

    public static void main(String[] args) {
        final int port = parseInt(getenv().get("PORT"));
        port(port);
        enableCORS();
        Stream.of(LoginEndpoint.create(),
                  RecordEndpoint.create())
              .forEach(Endpoint::serve);
    }

    private static void enableCORS() {
        before((request, response) -> {
            final String frontend = "https://obscure-lowlands-96477.herokuapp.com";
            response.header("Access-Control-Allow-Origin", frontend);
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
            response.header("Access-Control-Allow-Credentials", "true");
            response.type("application/json");
        });
    }
}
