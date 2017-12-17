package edu.ddashenkov.plumbum.deploy.endpoint;

import edu.ddashenkov.plumbum.adapter.Points;
import edu.ddashenkov.plumbum.deploy.client.PlumbumClient;
import edu.ddashenkov.plumbum.record.AppendText;
import edu.ddashenkov.plumbum.record.CreateRecord;
import edu.ddashenkov.plumbum.record.Point;
import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import io.spine.core.UserId;
import io.spine.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static edu.ddashenkov.plumbum.deploy.client.PlumbumClient.instance;
import static java.lang.Long.parseLong;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public final class RecordEndpoint implements Endpoint {

    private static final String ID_PARAM = ":id";
    private static final DateTimeFormatter DEFAULT_NAME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm d MMM YYYY");

    private RecordEndpoint() {
        // Prevent direct instantiation.
    }

    public static Endpoint create() {
        return new RecordEndpoint();
    }

    @Override
    public void serve() {
        get("/records", (request, response) -> {
            log().info("Fetching records");
            final PlumbumClient client = client(request);
            final RecordList records = client.getMyRecords();
            return records;
        }, toJson());

        get("/record/" + ID_PARAM, (request, response) -> {
            final PlumbumClient client = client(request);
            final long recordId = parseLong(request.params(ID_PARAM));
            log().info("Fetching record " + recordId);
            final Record record = client.getRecord(recordId(recordId));
            return record;
        }, toJson());
        put("/record/" + ID_PARAM, (request, response) -> {
            final PlumbumClient client = client(request);
            final long recordId = parseLong(request.params(ID_PARAM));
            log().info("Creating record " + recordId);
            final LocalDateTime time = LocalDateTime.now();
            final CreateRecord command = CreateRecord.newBuilder()
                                                     .setId(recordId(recordId))
                                                     .setDisplayName(time.format(DEFAULT_NAME_FORMATTER))
                                                     .build();
            client.createRecord(command);
            return "OK";
        });
        post("/record/" + ID_PARAM, (request, response) -> {
            try {
                final PlumbumClient client = client(request);
                final long recordId = parseLong(request.params(ID_PARAM));
                log().info("Appending to record " + recordId);
                final String body = request.body();
                final Collection<Point> points = parse(body);
                final AppendText command = AppendText.newBuilder()
                                                     .setId(recordId(recordId))
                                                     .addAllNewPoints(points)
                                                     .build();
                client.appendText(command);
                return "OK";
            } catch (RuntimeException e) {
                log().error("Error", e);
                throw e;
            }
        });
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

    private Collection<Point> parse(String requestBody) {
        final Points points = Json.fromJson(requestBody, Points.class);
        return points.getPointsList();
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(RecordEndpoint.class);
    }
}
