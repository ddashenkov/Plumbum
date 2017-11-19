package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.client.AnonymousClient;
import edu.ddashenkov.plumbum.user.CreateUser;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.spine.Identifier;
import io.spine.core.Ack;
import io.spine.core.UserId;
import io.spine.people.PersonName;

import static com.google.common.base.Preconditions.checkState;
import static io.spine.core.Status.StatusCase.OK;
import static spark.Spark.get;

final class LoginController implements Controller {

    private final Channel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                                                         .build();

    private final AnonymousClient client = AnonymousClient.instance(channel);

    private LoginController() {
        // Prevent direct instantiation.
    }

    static Controller create() {
        return new LoginController();
    }

    @Override
    public void serve() {
        get("/signup", (request, response) -> {
            final UserId userId = newUser();
            final String name = Header.USERNAME.get(request);
            final String password = Header.PASSWORD.get(request);
            final CreateUser command = CreateUser.newBuilder()
                                                 .setUserId(userId)
                                                 .setPassword(password)
                                                 .setName(name(name))
                                                 .build();
            final Ack ack = client.createUser(command);
            checkState(ack.getStatus().getStatusCase() == OK);
            return userId.getValue();
        });

        get("/login", (request, response) -> null);
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
}
