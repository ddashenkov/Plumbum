package edu.ddashenkov.plumbum.webadapter;

import edu.ddashenkov.plumbum.user.CreateUser;
import edu.ddashenkov.plumbum.user.User;
import io.spine.client.Query;
import io.spine.core.Ack;
import io.spine.core.UserId;

import java.util.Optional;

public final class AnonymousClient extends AbstractClient {

    private static final UserId ANONYMOUS = UserId.newBuilder()
                                                  .setValue("-")
                                                  .build();

    private AnonymousClient() {
        super(ANONYMOUS);
    }

    public Ack createUser(CreateUser command) {
        return sendCommand(command);
    }

    public Optional<UserId> checkUser(String username, String password) {
        final Query query = requestFactory().query()
                                            .all(User.class);
        final Optional<User> profile = this.<User>read(query).parallelStream()
                                                             .unordered()
                                                             .filter(user -> user.getName()
                                                                                 .getGivenName()
                                                                                 .equals(username))
                                                             .findAny()
                                                             .filter(user -> user.getPassword()
                                                                                 .equals(password));
        return profile.map(User::getId);
    }

    public static AnonymousClient instance() {
        return Singleton.INSTANCE.value;
    }

    private enum Singleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final AnonymousClient value = new AnonymousClient();
    }
}
