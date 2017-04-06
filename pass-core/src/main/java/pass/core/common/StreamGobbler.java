package pass.core.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StreamGobbler implements Runnable
{

    private final static Logger LOGGER = Logger.getLogger(StreamGobbler.class.getName());

    private final InputStream stream;
    private final LineVisitor lineVisitor;

    public StreamGobbler(InputStream stream)
    {
        this.stream = stream;
        this.lineVisitor = null;
    }

    public StreamGobbler(InputStream stream, LineVisitor lineVisitor)
    {
        this.stream = stream;
        this.lineVisitor = lineVisitor;
    }

    @Override
    public void run()
    {
        InputStreamReader isr = new InputStreamReader(stream);
        try (BufferedReader reader = new BufferedReader(isr)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineVisitor != null) {
                    lineVisitor.visitLine(line);
                }
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
