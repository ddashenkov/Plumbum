package edu.ddashenkov.plumbum.deploy.endpoint;

import spark.Request;

/**
 * @author Dmytro Dashenkov
 */
enum Header implements RequestProperty {
    USER_ID("userId"),
    USERNAME("username"),
    PASSWORD("password");

    private final String name;

    Header(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(Request request) {
        return request.headers(getName());
    }
}
