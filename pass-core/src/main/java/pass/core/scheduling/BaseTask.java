package pass.core.scheduling;

import java.util.UUID;

public abstract class BaseTask implements Runnable
{

    protected final UUID taskId;
    protected final TaskSpecification taskSpec;
    protected final StatusUpdater statusUpdater;
    protected int threadPriority;

    public BaseTask(UUID taskId,
                    TaskSpecification taskSpec,
                    StatusUpdater statusUpdater)
    {
        this.taskId = taskId;
        this.statusUpdater = statusUpdater;
        this.taskSpec = taskSpec;
        this.threadPriority = Thread.NORM_PRIORITY;
    }

    public UUID getTaskId()
    {
        return taskId;
    }

    protected void updateStatus(Status.TaskState state, String msg)
    {
        Status status = new Status(state, msg);
        statusUpdater.send(taskId, taskSpec, status);
    }

    @Override
    public void run()
    {
        Thread.currentThread().setPriority(threadPriority);
        updateStatus(Status.TaskState.RUNNING, "Started");
        try {
            runTask();
        }
        finally {
            updateStatus(Status.TaskState.FINISHED, null);
        }
    }

    abstract void runTask();
}
