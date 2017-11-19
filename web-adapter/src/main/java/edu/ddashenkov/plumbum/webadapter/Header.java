package edu.ddashenkov.plumbum.webadapter;

import spark.Request;

/**
 * @author Dmytro Dashenkov
 */
enum Header implements RequestProperty {

    PASSWORD("password"),
    USERNAME("name");

    private String name;

    Header(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(Request request) {
        return request.headers(name);
    }
}
