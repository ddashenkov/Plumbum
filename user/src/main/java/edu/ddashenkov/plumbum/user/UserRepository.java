package edu.ddashenkov.plumbum.user;

import io.spine.core.UserId;
import io.spine.server.aggregate.AggregateRepository;

/**
 * @author Dmytro Dashenkov
 */
public class UserRepository extends AggregateRepository<UserId, UserAggregate> {
}
