package edu.ddashenkov.plumbum.webadapter;

import spark.Request;

interface RequestProperty {
    String getName();
    String get(Request request);
}
