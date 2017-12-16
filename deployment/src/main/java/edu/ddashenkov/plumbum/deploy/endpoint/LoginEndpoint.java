package edu.ddashenkov.plumbum.deploy.endpoint;

import com.google.common.base.Throwables;
import edu.ddashenkov.plumbum.user.CreateUser;
import edu.ddashenkov.plumbum.deploy.client.AnonymousClient;
import io.spine.Identifier;
import io.spine.core.Ack;
import io.spine.core.UserId;
import io.spine.people.PersonName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static io.spine.core.Status.StatusCase.OK;
import static spark.Spark.exception;
import static spark.Spark.get;

public final class LoginEndpoint implements Endpoint {

    private final AnonymousClient client = AnonymousClient.instance();

    private LoginEndpoint() {
        // Prevent direct instantiation.
    }

    public static Endpoint create() {
        return new LoginEndpoint();
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
            final Optional<String> userId = client.checkUser(name, password)
                                                  .map(UserId::getValue);
            if (userId.isPresent()) {
                log().info("Log in User {}", userId);
                Cookie.USER_ID.set(response, userId.get());
                return userId;
            } else {
                response.status(401);
                return "";
            }
        }, toJson());

        exception(Exception.class, (e, request, response) -> {
            response.status(500);
            response.body("{ 'error': '"  + Throwables.getStackTraceAsString(e) + "' }");
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

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(LoginEndpoint.class);
    }
}
