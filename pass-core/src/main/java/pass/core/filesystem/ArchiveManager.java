package pass.core.filesystem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass.core.common.Util;

public class ArchiveManager
{

    public class ArchiveInfo
    {

        private final String name;
        private final long size;
        private final Date created;

        public ArchiveInfo(String name, long size, Date created)
        {
            this.name = name;
            this.size = size;
            this.created = created;
        }

        public String getName()
        {
            return name;
        }

        public long getSize()
        {
            return size;
        }

        public String getSizeHumanReadable()
        {
            return Util.fileSizeHumanReadable(size);
        }

        public Date getCreated()
        {
            return created;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ArchiveManager.class.getName());

    public List<ArchiveInfo> listArchives()
    {
        List<ArchiveInfo> list = new ArrayList<>();
        Path archiveDir = FileRepository.getArchiveDirectory();
        try {
            Files.createDirectories(archiveDir);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(archiveDir)) {
                for (Path p : stream) {
                    BasicFileAttributes attr;
                    attr = Files.readAttributes(p, BasicFileAttributes.class);
                    ArchiveInfo ai;
                    ai = new ArchiveInfo(p.getFileName().toString(),
                                         attr.size(),
                                         new Date(attr.creationTime()
                                                 .toMillis()));
                    list.add(ai);
                }
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Path getArchive(String name)
    {
        return FileRepository.getArchiveDirectory().resolve(name);
    }
}
