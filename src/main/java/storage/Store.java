package storage;

public class Store {
    private static InMemoryInstanceRepository storage;

    public static void setToInMemory() {
        storage = new InMemoryInstanceRepository();
    }

    public static InMemoryInstanceRepository get() {
        if (storage == null) {
            throw new IllegalStateException("Repository not initialized. Call setToInMemory() first.");
        }
        return storage;
    }
}
