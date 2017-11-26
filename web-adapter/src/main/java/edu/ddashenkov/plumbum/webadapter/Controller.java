package edu.ddashenkov.plumbum.webadapter;

import spark.ResponseTransformer;

import static io.spine.json.Json.toCompactJson;
import static io.spine.protobuf.TypeConverter.toMessage;

/**
 * @author Dmytro Dashenkov
 */
interface Controller {

    void serve();

    default ResponseTransformer toJson() {
        return object -> object == null ? "null" : toCompactJson(toMessage(object));
    }
}
