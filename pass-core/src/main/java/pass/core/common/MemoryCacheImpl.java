package pass.core.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MemoryCacheImpl implements MemoryCache
{

    private final AtomicReference<Map<String, Object>> mapRef;

    public MemoryCacheImpl()
    {
        Map<String, Object> map = new HashMap<>();
        this.mapRef = new AtomicReference<>(map);
    }

    @Override
    public void put(String key, Object value)
    {
        Map<String, Object> newMap = new HashMap<>(mapRef.get());
        newMap.put(key, value);
        mapRef.set(newMap);
    }

    @Override
    public void invalidate(String key)
    {
        Map<String, Object> newMap = new HashMap<>(mapRef.get());
        newMap.remove(key);
        mapRef.set(newMap);
    }

    @Override
    public Object get(String key)
    {
        return mapRef.get().get(key);
    }
}
