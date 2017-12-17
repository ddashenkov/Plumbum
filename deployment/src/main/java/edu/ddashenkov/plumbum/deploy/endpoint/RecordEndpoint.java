package edu.ddashenkov.plumbum.deploy.endpoint;

import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import edu.ddashenkov.plumbum.deploy.client.PlumbumClient;
import io.spine.core.UserId;
import spark.Request;

import static edu.ddashenkov.plumbum.deploy.client.PlumbumClient.instance;
import static java.lang.Long.parseLong;
import static spark.Spark.get;

public final class RecordEndpoint implements Endpoint {

    private static final String ID_PARAM = ":id";

    private RecordEndpoint() {
        // Prevent direct instantiation.
    }

    public static Endpoint create() {
        return new RecordEndpoint();
    }

    @Override
    public void serve() {
        get("/records", (request, response) -> {
            final PlumbumClient client = client(request);
            final RecordList records = client.getMyRecords();
            return records;
        }, toJson());
        get("/record/" + ID_PARAM, (request, response) -> {
            final PlumbumClient client = client(request);
            final long recordId = parseLong(request.params(ID_PARAM));
            final Record record = client.getRecord(recordId(recordId));
            return record;
        }, toJson());
    }

    private PlumbumClient client(Request request) {
        final String userId = Header.USER_ID.get(request);
        final UserId user = UserId.newBuilder()
                                  .setValue(userId)
                                  .build();
        return instance(user);
    }

    private static RecordId recordId(long value) {
        return RecordId.newBuilder()
                       .setUid(value)
                       .build();
    }
}
