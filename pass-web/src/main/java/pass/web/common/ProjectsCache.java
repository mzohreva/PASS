package pass.web.common;

import java.util.List;
import pass.core.common.MemoryCache;
import pass.core.common.MemoryCacheImpl;
import pass.core.hibernate.HibernateSession;
import pass.core.hibernate.ProjectsRepository;
import pass.core.model.Project;

public class ProjectsCache
{

    private static final String KEY_ALL = "PALL";
    private static final String KEY_VISIBLE = "PVIS";

    private static final ProjectsCache instance = new ProjectsCache();

    public static ProjectsCache getInstance()
    {
        return instance;
    }

    private final MemoryCache cache;

    private ProjectsCache()
    {
        this.cache = new MemoryCacheImpl();
    }

    private void retrieveFromDb()
    {
        try (HibernateSession hs = new HibernateSession()) {
            ProjectsRepository repo = new ProjectsRepository(hs);
            List<Project> all = repo.list(/* onlyVisible: */false);
            List<Project> vis = repo.list(/* onlyVisible: */true);
            cache.put(KEY_ALL, all);
            cache.put(KEY_VISIBLE, vis);
        }
    }

    private Object get(String key)
    {
        Object o = cache.get(key);
        if (o == null) {
            retrieveFromDb();
            o = cache.get(key);
        }
        return o;
    }

    @SuppressWarnings ("unchecked")
    public List<Project> listAllProjects()
    {
        return (List<Project>) get(KEY_ALL);
    }

    @SuppressWarnings ("unchecked")
    public List<Project> listVisibleProjects()
    {
        return (List<Project>) get(KEY_VISIBLE);
    }

    public void invalidate()
    {
        cache.invalidate(KEY_ALL);
        cache.invalidate(KEY_VISIBLE);
    }
}
