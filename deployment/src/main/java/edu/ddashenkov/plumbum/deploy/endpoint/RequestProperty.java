package edu.ddashenkov.plumbum.deploy.endpoint;

import spark.Request;

interface RequestProperty {
    String getName();
    String get(Request request);
}
