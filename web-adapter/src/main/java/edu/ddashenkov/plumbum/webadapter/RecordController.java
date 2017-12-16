package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import io.spine.core.UserId;
import spark.Request;

import static edu.ddashenkov.plumbum.webadapter.PlumbumClient.instance;
import static java.lang.Long.parseLong;
import static spark.Spark.get;

final class RecordController implements Controller {

    private static final String ID_PARAM = ":id";

    private RecordController() {
        // Prevent direct instantiation.
    }

    static Controller create() {
        return new RecordController();
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
        final String userId = Cookie.USER_ID.get(request);
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
