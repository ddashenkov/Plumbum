package edu.ddashenkov.plumbum.deploy.endpoint;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import edu.ddashenkov.plumbum.deploy.client.PlumbumClient;
import edu.ddashenkov.plumbum.record.AppendText;
import edu.ddashenkov.plumbum.record.CreateRecord;
import edu.ddashenkov.plumbum.record.DeleteRecord;
import edu.ddashenkov.plumbum.record.Point;
import edu.ddashenkov.plumbum.record.Record;
import edu.ddashenkov.plumbum.record.RecordId;
import edu.ddashenkov.plumbum.record.RecordList;
import io.spine.core.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static edu.ddashenkov.plumbum.deploy.client.PlumbumClient.instance;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public final class RecordEndpoint implements Endpoint {

    private static final String ID_PARAM = ":id";
    private static final String RECROD_NAME_PARAM = "name";
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
            try {
                String recordName = request.queryParams(RECROD_NAME_PARAM);
                if (isNullOrEmpty(recordName)) {
                    final LocalDateTime time = LocalDateTime.now();
                    recordName = time.format(DEFAULT_NAME_FORMATTER);
                }
                final PlumbumClient client = client(request);
                final long recordId = parseLong(request.params(ID_PARAM));
                log().info("Creating record " + recordId);
                final CreateRecord command = CreateRecord.newBuilder()
                                                         .setId(recordId(recordId))
                                                         .setDisplayName(recordName)
                                                         .build();
                log().info("{}", command);
                client.createRecord(command);
                return "OK";
            } catch (RuntimeException e) {
                log().error("Error", e);
                throw e;
            }
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
                log().info("{}", command);
                client.appendText(command);
                return "OK";
            } catch (RuntimeException e) {
                log().error("Error", e);
                throw e;
            }
        });
        delete("/record/" + ID_PARAM, (request, response) -> {
            try {
                final PlumbumClient client = client(request);
                final long recordId = parseLong(request.params(ID_PARAM));
                log().info("Deleting record " + recordId);
                final DeleteRecord command = DeleteRecord.newBuilder()
                                                       .setId(recordId(recordId))
                                                       .build();
                log().info("{}", command);
                client.deleteRecord(command);
                return "OK";
            } catch (RuntimeException e) {
                log().error("Error", e);
                throw e;
            }
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

    private List<Point> parse(String requestBody) {
        final Type type = new TypeToken<List<AdapterPoint>>() {}.getType();
        final Gson gson = new Gson();
        final List<AdapterPoint> adapterPoints = gson.fromJson(requestBody, type);
        final List<Point> result = adapterPoints.stream()
                                                .map(point -> Point.newBuilder()
                                                                   .setX(point.x)
                                                                   .setY(point.y)
                                                                   .build())
                                                .collect(toList());
        return result;
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(RecordEndpoint.class);
    }

    private static final class AdapterPoint {

        private float x;
        private float y;
    }
}
