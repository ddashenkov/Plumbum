package edu.ddashenkov.plumbum.deploy.endpoint;

import spark.Request;
import spark.Response;

/**
 * @author Dmytro Dashenkov
 */
enum Cookie implements RequestProperty {
    USER_ID("userId"),
    USERNAME("name"),
    PASSWORD("password");

    private final String name;

    Cookie(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(Request request) {
        return request.cookies()
                      .get(getName());
    }

    public void set(Response response, String value) {
        response.cookie(getName(), value);
    }
}