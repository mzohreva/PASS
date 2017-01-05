package pass.core.common;

public interface MemoryCache
{

    void put(String key, Object value);

    void invalidate(String key);

    Object get(String key);

}
