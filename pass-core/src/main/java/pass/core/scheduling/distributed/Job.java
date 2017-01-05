package pass.core.scheduling.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.scheduling.TaskSpecification;

public class Job implements Serializable
{

    private static final Logger LOGGER = Logger.getLogger(Job.class.getName());

    private final UUID taskId;
    private final TaskSpecification taskSpec;

    public Job(UUID taskId, TaskSpecification taskSpec)
    {
        this.taskId = taskId;
        this.taskSpec = taskSpec;
    }

    public UUID getTaskId()
    {
        return taskId;
    }

    public TaskSpecification getTaskSpec()
    {
        return taskSpec;
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

    public static Job deserialize(byte[] input)
    {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Job) ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
