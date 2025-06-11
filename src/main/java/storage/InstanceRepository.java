package storage;

public interface InstanceRepository {
    <T> void save(String key, T obj, Class<T> type);
    <T> java.util.List<T> getAll(String key);
    <T> void clear(String key);
}
