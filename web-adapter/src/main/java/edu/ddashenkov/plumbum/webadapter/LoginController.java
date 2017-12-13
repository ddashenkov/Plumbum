package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.client.AnonymousClient;
import edu.ddashenkov.plumbum.client.Channels;
import edu.ddashenkov.plumbum.user.CreateUser;
import io.spine.Identifier;
import io.spine.core.Ack;
import io.spine.core.UserId;
import io.spine.people.PersonName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkState;
import static io.spine.core.Status.StatusCase.OK;
import static spark.Spark.get;

final class LoginController implements Controller {

    private final AnonymousClient client = AnonymousClient.instance(Channels.getDefault());

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
            final String name = Cookie.USERNAME.get(request);
            final String password = Cookie.PASSWORD.get(request);
            log().info("Sign up User {} (password: {}). Assigned ID {}.", name, password, userId);
            final CreateUser command = CreateUser.newBuilder()
                                                 .setUserId(userId)
                                                 .setPassword(password)
                                                 .setName(name(name))
                                                 .build();
            final Ack ack = client.createUser(command);
            checkState(ack.getStatus().getStatusCase() == OK);
            Cookie.USER_ID.set(response, userId.getValue());
            return userId;
        }, toJson());
        get("/login", (request, response) -> {
            final String name = Cookie.USERNAME.get(request);
            final String password = Cookie.PASSWORD.get(request);
            final String userId = client.checkUser(name, password)
                                        .map(UserId::getValue)
                                        .orElse("");
            log().info("Log in User {}", userId);
            Cookie.USER_ID.set(response, userId);
            return userId;
        }, toJson());
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

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(LoginController.class);
    }
}
