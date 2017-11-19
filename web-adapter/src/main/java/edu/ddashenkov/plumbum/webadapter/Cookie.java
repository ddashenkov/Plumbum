package edu.ddashenkov.plumbum.webadapter;

import spark.Request;
import spark.Response;

/**
 * @author Dmytro Dashenkov
 */
enum Cookie implements RequestProperty {
    USER_ID("userId");

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
