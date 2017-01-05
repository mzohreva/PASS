package pass.core.scheduling;

import java.util.UUID;

public interface StatusUpdater
{

    void send(UUID taskId, TaskSpecification taskSpec, Status status);

}
