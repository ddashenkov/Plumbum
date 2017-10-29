package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.user.CreateUser;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.spine.Identifier;
import io.spine.client.ActorRequestFactory;
import io.spine.client.grpc.CommandServiceGrpc;
import io.spine.client.grpc.CommandServiceGrpc.CommandServiceBlockingStub;
import io.spine.client.grpc.QueryServiceGrpc;
import io.spine.client.grpc.QueryServiceGrpc.QueryServiceBlockingStub;
import io.spine.core.Ack;
import io.spine.core.Command;
import io.spine.core.UserId;
import io.spine.people.PersonName;
import io.spine.time.ZoneOffset;
import io.spine.time.ZoneOffsets;
import spark.Request;

import java.text.ParseException;

import static spark.Spark.get;

/**
 * @author Dmytro Dashenkov
 */
public class LoginController {

    private static final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                                                                       .build();
    private static final CommandServiceBlockingStub commandService = CommandServiceGrpc.newBlockingStub(channel);
    private static final QueryServiceBlockingStub queryService = QueryServiceGrpc.newBlockingStub(channel);

    public static void main(String[] args) {
        listenSignUp();
    }

    private static void listenSignUp() {
        get("/signup", (request, response) -> {
            final UserId userId = newUser();
            final String timeZone = Header.TIMEZONE.get(request);
            final ActorRequestFactory factory = requestFactory(userId, timeZone);
            final String name = Header.USERNAME.get(request);
            final String password = Header.PASSWORD.get(request);
            final CreateUser command = CreateUser.newBuilder()
                                                 .setUserId(userId)
                                                 .setPassword(password)
                                                 .setName(name(name))
                                                 .build();
            final Command cmd = factory.command().create(command);
            final Ack ack = commandService.post(cmd);
            return userId.getValue();
        });
    }

    private static UserId newUser() {
        return UserId.newBuilder()
                     .setValue(Identifier.newUuid())
                     .build();
    }

    private static PersonName name(String name) {
        return PersonName.newBuilder()
                         .setGivenName(name)
                         .build();
    }

    private static ActorRequestFactory requestFactory(UserId userId, String timeZone) throws ParseException {
        final ZoneOffset zoneOffset = ZoneOffsets.parse(timeZone);
        final ActorRequestFactory factory = ActorRequestFactory.newBuilder()
                                                               .setActor(userId)
                                                               .setZoneOffset(zoneOffset)
                                                               .build();
        return factory;
    }

    private enum Header {

        PASSWORD("password"),
        USERNAME("name"),
        TIMEZONE("tzone");

        private String name;

        Header(String name) {
            this.name = name;
        }

        private String get(Request request) {
            return request.headers(name);
        }
    }
}
