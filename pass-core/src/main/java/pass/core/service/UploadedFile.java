package pass.core.service;

import java.io.IOException;
import java.io.InputStream;

/*
 * This class is used to remove dependency on javax.servlet.http.Part
 * from the service layer
 */
public class UploadedFile
{

    private final InputStream inputStream;
    private final String contentType;
    private final String submittedFileName;
    private final long size;

    public UploadedFile(InputStream inputStream,
                        String contentType,
                        String submittedFileName,
                        long size)
    {
        this.inputStream = inputStream;
        this.contentType = contentType;
        this.submittedFileName = submittedFileName;
        this.size = size;
    }

    public InputStream getInputStream() throws IOException
    {
        return inputStream;
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getSubmittedFileName()
    {
        return submittedFileName;
    }

    public long getSize()
    {
        return size;
    }
}
