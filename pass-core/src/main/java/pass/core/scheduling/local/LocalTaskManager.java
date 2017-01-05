package pass.core.scheduling.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.BaseTask;
import pass.core.scheduling.Status;
import pass.core.scheduling.StatusUpdater;
import pass.core.scheduling.TaskManager;
import pass.core.scheduling.TaskSpecification;
import pass.core.scheduling.TaskStatus;
import pass.core.scheduling.TaskType;

public class LocalTaskManager implements TaskManager
{

    private static final Logger LOGGER = Logger.getLogger(LocalTaskManager.class.getName());

    private final ThreadPoolExecutor executor;
    private final Map<UUID, TaskStatus> statusMap;
    private final BlockingQueue<TaskStatus> mQ;
    private final StatusUpdater statusUpdater;
    private final Map<TaskType, Long> completedTasks;

    /*
     * This class should be instantiated in a ServletContextListener and
     * the instance should be maintained during container's life-cycle
     */
    public LocalTaskManager(int nThreads)
    {
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        this.statusMap = new HashMap<>();
        this.mQ = new LinkedBlockingQueue<>();
        this.statusUpdater = new LocalStatusUpdater(mQ);
        this.completedTasks = new HashMap<>();
        LOGGER.log(Level.INFO, "Initialized with {0} threads.", nThreads);
    }

    private void executeTask(TaskSpecification taskSpec)
    {
        UUID taskId = UUID.randomUUID();
        BaseTask task = taskSpec.makeCorrespondingTask(taskId, statusUpdater);
        Status status = new Status(Status.TaskState.QUEUED, null);
        mQ.offer(new TaskStatus(taskId, taskSpec, "n/a", status));
        executor.execute(task);
    }

    @Override
    public void evaluateSubmission(int submissionId)
    {
        TaskSpecification ts = TaskSpecification.evaluation(submissionId);
        executeTask(ts);
    }

    @Override
    public void cleanupSubmissions(String username, int projectId)
    {
        TaskSpecification ts = TaskSpecification.cleanup(username, projectId);
        executeTask(ts);
    }

    @Override
    public void zipSubmissions(int projectId)
    {
        TaskSpecification ts = TaskSpecification.zip(projectId);
        executeTask(ts);
    }

    private void processMessages()
    {
        int n = mQ.size();
        while (n > 0) {
            TaskStatus m = mQ.poll();
            if (m != null) {
                statusMap.put(m.getTaskId(), m);
                if (m.getStatus().getState() == Status.TaskState.FINISHED) {
                    statusMap.remove(m.getTaskId());
                    TaskType taskType = m.getTaskSpec().getType();
                    Long x = completedTasks.get(taskType);
                    completedTasks.put(taskType, (x == null ? 1L : x + 1));
                }
            }
            n--;
        }
    }

    @Override
    public Status getEvaluateSubmissionTaskStatus(int submissionId)
    {
        processMessages();
        for (Map.Entry<UUID, TaskStatus> kv : statusMap.entrySet()) {
            TaskSpecification ts = kv.getValue().getTaskSpec();
            if (ts.isSubmissionEvaluation(submissionId)) {
                return kv.getValue().getStatus();
            }
        }
        return null;
    }

    private List<TaskStatus> listTasksByState(Status.TaskState state)
    {
        processMessages();
        List<TaskStatus> list = new ArrayList<>();
        statusMap.entrySet().stream()
                .filter((kv) -> (kv.getValue().getStatus().getState() == state))
                .forEachOrdered((kv) -> {
                    list.add(kv.getValue());
                });
        return list;
    }

    @Override
    public List<TaskStatus> listQueuedTasks()
    {
        return listTasksByState(Status.TaskState.QUEUED);
    }

    @Override
    public List<TaskStatus> listRunningTasks()
    {
        return listTasksByState(Status.TaskState.RUNNING);
    }

    @Override
    public List<TaskStatus> listTasksByType(TaskType taskType)
    {
        processMessages();
        List<TaskStatus> list = new ArrayList<>();
        statusMap.entrySet().stream()
                .filter((kv) -> (kv.getValue().getTaskSpec().getType() == taskType))
                .forEachOrdered((kv) -> {
                    list.add(kv.getValue());
                });
        return list;
    }

    @Override
    public long completedTasksByType(TaskType taskType)
    {
        processMessages();
        Long x = completedTasks.get(taskType);
        return (x == null ? 0L : x);
    }

    @Override
    public void shutdown()
    {
        try {
            executor.shutdown();
            LOGGER.info("Waiting for executor to terminate...");
            boolean terminated = executor.awaitTermination(60,
                                                           TimeUnit.SECONDS);
            if (!terminated) {
                executor.shutdownNow();
                LOGGER.warning("Executor was not terminated properly");
            }
            else {
                LOGGER.info("Shutdown complete.");
            }
        }
        catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE,
                       "Interrupted while waiting for executor",
                       ex);
        }
    }
}
