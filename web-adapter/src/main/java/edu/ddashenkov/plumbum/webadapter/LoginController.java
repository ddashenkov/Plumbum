package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.client.PlumbumClient;
import edu.ddashenkov.plumbum.user.CreateUser;
import io.spine.Identifier;
import io.spine.core.UserId;
import io.spine.people.PersonName;
import spark.Request;

import static spark.Spark.get;

/**
 * @author Dmytro Dashenkov
 */
public class LoginController {

    private static final PlumbumClient client = PlumbumClient.anonymous("localhost", 50051);

    public static void main(String[] args) {
        listenSignUp();
    }

    private static void listenSignUp() {
        get("/signup", (request, response) -> {
            final UserId userId = newUser();
            final String name = Header.USERNAME.get(request);
            final String password = Header.PASSWORD.get(request);
            final CreateUser command = CreateUser.newBuilder()
                                                 .setUserId(userId)
                                                 .setPassword(password)
                                                 .setName(name(name))
                                                 .build();
            client.createUser(command);
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
