package edu.ddashenkov.plumbum.deploy;

import edu.ddashenkov.plumbum.deploy.endpoint.Endpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.LoginEndpoint;
import edu.ddashenkov.plumbum.deploy.endpoint.RecordEndpoint;

import java.util.stream.Stream;

import static spark.Spark.before;

final class Starter {

    /**
     * The {@code private} constructor prevents the utility class instantiation.
     */
    private Starter() {
    }

    public static void main(String[] args) {
        enableCORS();
        Stream.of(LoginEndpoint.create(),
                  RecordEndpoint.create())
              .forEach(Endpoint::serve);
    }

    private static void enableCORS() {
        before((request, response) -> {
            final String frontend = "https://obscure-lowlands-96477.herokuapp.com";
            response.header("Access-Control-Allow-Origin", frontend);
            response.header("Access-Control-Request-Method", frontend);
            response.header("Access-Control-Allow-Headers", frontend);
            response.header("Access-Control-Allow-Credentials", "true");
            response.type("application/json");
        });
    }
}
