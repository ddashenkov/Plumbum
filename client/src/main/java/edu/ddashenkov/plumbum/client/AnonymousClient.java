package edu.ddashenkov.plumbum.client;

import edu.ddashenkov.plumbum.user.CreateUser;
import io.grpc.Channel;
import io.spine.core.Ack;
import io.spine.core.UserId;

public final class AnonymousClient extends AbstractClient {

    private static final UserId ANONYMOUS = UserId.newBuilder()
                                                  .setValue("-")
                                                  .build();

    private AnonymousClient(Channel channel) {
        super(channel, ANONYMOUS);
    }

    public static AnonymousClient instance(Channel channel) {
        return new AnonymousClient(channel);
    }

    public Ack createUser(CreateUser command) {
        return sendCommand(command);
    }
}
