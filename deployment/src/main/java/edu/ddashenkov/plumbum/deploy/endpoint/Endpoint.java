package edu.ddashenkov.plumbum.deploy.endpoint;

import spark.ResponseTransformer;

import static io.spine.json.Json.toCompactJson;
import static io.spine.protobuf.TypeConverter.toMessage;

/**
 * @author Dmytro Dashenkov
 */
public interface Endpoint {

    void serve();

    default ResponseTransformer toJson() {
        return object -> object == null ? "null" : toCompactJson(toMessage(object));
    }
}
