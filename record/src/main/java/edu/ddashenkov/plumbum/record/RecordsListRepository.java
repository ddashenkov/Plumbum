package edu.ddashenkov.plumbum.record;

import io.spine.core.UserId;
import io.spine.server.projection.ProjectionRepository;

import static java.util.Collections.singleton;

public class RecordsListRepository extends ProjectionRepository<UserId, RecordsListProjection, RecordList> {

    public RecordsListRepository() {
        super();
        getEventRouting().replaceDefault((message, context) -> singleton(context.getCommandContext()
                                                                                .getActorContext()
                                                                                .getActor()));
    }
}
