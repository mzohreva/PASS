package pass.core.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class UnixStyleLogFormatter extends Formatter
{

    private final DateFormat dateFormat;
    private final boolean brief;
    private final boolean showStackTrace;

    public UnixStyleLogFormatter(boolean brief, boolean showStackTrace)
    {
        super();
        this.dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        this.brief = brief;
        this.showStackTrace = showStackTrace;
    }

    @Override
    public String format(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();

        Date date = new Date(record.getMillis());
        sb.append(dateFormat.format(date)).append(" ");
        if (!brief) {
            sb.append(record.getLevel().getName()).append(" ");
            sb.append(record.getLoggerName()).append(": ");
        }
        sb.append(formatMessage(record));

        Throwable t = record.getThrown();
        if (t != null) {
            sb.append(" ");
            if (showStackTrace) {
                StringWriter sink = new StringWriter();
                t.printStackTrace(new PrintWriter(sink, true));
                sb.append(sink.toString());
            }
            else {
                sb.append(t.toString());
            }
        }
        sb.append("\n");

        return sb.toString();
    }
}
