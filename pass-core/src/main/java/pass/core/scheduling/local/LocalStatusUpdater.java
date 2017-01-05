package pass.core.scheduling.local;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import pass.core.scheduling.Status;
import pass.core.scheduling.StatusUpdater;
import pass.core.scheduling.TaskSpecification;
import pass.core.scheduling.TaskStatus;

public class LocalStatusUpdater implements StatusUpdater
{

    private final BlockingQueue<TaskStatus> mQ;

    public LocalStatusUpdater(BlockingQueue<TaskStatus> mQ)
    {
        this.mQ = mQ;
    }

    @Override
    public void send(UUID taskId, TaskSpecification taskSpec, Status status)
    {
        TaskStatus message = new TaskStatus(taskId, taskSpec, "local", status);
        mQ.offer(message);
    }
}
