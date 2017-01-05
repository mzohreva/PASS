package pass.core.scheduling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskStatus implements Serializable
{

    private static final Logger LOGGER = Logger.getLogger(TaskStatus.class.getName());

    private final UUID taskId;
    private final TaskSpecification taskSpec;
    private final String worker;
    private final Status status;

    public TaskStatus(UUID taskId,
                      TaskSpecification taskSpec,
                      String worker,
                      Status status)
    {
        this.taskId = taskId;
        this.taskSpec = taskSpec;
        this.worker = worker;
        this.status = status;
    }

    public UUID getTaskId()
    {
        return taskId;
    }

    public TaskSpecification getTaskSpec()
    {
        return taskSpec;
    }

    public String getWorker()
    {
        return worker;
    }

    public Status getStatus()
    {
        return status;
    }

    public byte[] serialize()
    {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            return bos.toByteArray();
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static TaskStatus deserialize(byte[] input)
    {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (TaskStatus) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
