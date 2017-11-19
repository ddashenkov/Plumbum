package edu.ddashenkov.plumbum.record;

import io.spine.core.UserId;
import io.spine.server.projection.ProjectionRepository;

public class RecordsListRepository extends ProjectionRepository<UserId, RecordsListProjection, RecordList> {
}
