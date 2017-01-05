package pass.core.scheduling;

import java.io.Serializable;
import java.util.Date;

public class Status implements Serializable
{

    public enum TaskState
    {
        QUEUED("Queued"),
        RUNNING("Running"),
        FINISHED("Finished");

        private final String description;

        private TaskState(String description)
        {
            this.description = description;
        }

        public String getDescription()
        {
            return description;
        }
    }

    private final TaskState state;
    private final String message;
    private final Date time;

    public Status(TaskState state, String message)
    {
        this.state = state;
        this.message = message;
        this.time = new Date();
    }

    @Override
    public String toString()
    {
        return (message == null ? state.getDescription() : message);
    }

    public TaskState getState()
    {
        return state;
    }

    public String getMessage()
    {
        return message;
    }

    public Date getTime()
    {
        return time;
    }
}
