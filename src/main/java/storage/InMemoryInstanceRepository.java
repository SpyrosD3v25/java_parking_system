package storage;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import models.Pair;

/*
 * H skepsh pisw apo to storage einai oti tha kanoyme kapos "simulate" ena database
 * gia ayto xrhsimopoiw kai onomasies typos vehicle_ kai parking_spot_ 
 * 
 * Epishs mporoyme sto mellon na kanoyme "inject" allo ena diaforetiko eidos repository
 * kai otan kaloyme .save() na apothikeyontai se kapoio database, na xtypaei kapoy h otidhpote
 * einai etsi pio generic kai wraio ;)
 */
public class InMemoryInstanceRepository implements InstanceRepository {
    private final Map<String, List<Pair<Class<?>, Object>>> store = new HashMap<>();

    @Override
    public <T> void save(String key, T obj, Class<T> type) {
        List<Pair<Class<?>, Object>> list = store.computeIfAbsent(key, k -> new ArrayList<>());

        if (!list.isEmpty()) {
            Class<?> existingType = list.get(0).key;
            if (!existingType.equals(type)) {
                throw new IllegalArgumentException("Type mismatch for key '" + key +
                        "'. Expected: " + existingType.getName() + ", got: " + type.getName());
            }
        }

        list.add(new Pair<>(type, obj));
    }

    @Override
    public <T> List<T> getAll(String key) {
        List<Pair<Class<?>, Object>> rawList = store.getOrDefault(key, Collections.emptyList());
        List<T> result = new ArrayList<>();
        for (Pair<Class<?>, Object> pair : rawList) {
            @SuppressWarnings("unchecked")
            T value = (T) pair.value;
            result.add(value);
        }
        return result;
    }

    @Override
    public <T> void clear(String key) {
        store.remove(key);
    }
}
