package edu.ddashenkov.plumbum.user;

import io.spine.core.UserId;
import io.spine.server.aggregate.Aggregate;
import io.spine.server.aggregate.Apply;
import io.spine.server.command.Assign;

@SuppressWarnings("unused") // Reflective access.
public class UserAggregate extends Aggregate<UserId, User, UserVBuilder> {

    protected UserAggregate(UserId id) {
        super(id);
    }

    @Assign
    UserCreated handle(CreateUser command) {
        final UserCreated event = UserCreated.newBuilder()
                                             .setUserId(command.getUserId())
                                             .setName(command.getName())
                                             .setPassword(command.getPassword())
                                             .build();
        return event;
    }

    @Assign
    UserNameChanged handle(ChangeUserName command) {
        final UserNameChanged event = UserNameChanged.newBuilder()
                                                     .setUserId(command.getUserId())
                                                     .setName(command.getName())
                                                     .build();
        return event;
    }

    @Apply
    private void on(UserCreated event) {
        getBuilder()
                .setId(event.getUserId())
                .setName(event.getName())
                .setPassword(event.getPassword());
    }

    @Apply
    private void on(UserNameChanged event) {
        getBuilder().setName(event.getName());
    }
}
