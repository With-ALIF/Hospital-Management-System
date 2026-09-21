package com.hospital.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable, low level helper that moves data between Java objects and a JSON file.
 *
 * <pre>
 * JSON file      --load-->  Java objects      (loadList / loadObject)
 * Java objects   --save-->  JSON file         (saveList / save)
 * </pre>
 *
 * The class is defensive on purpose: a missing file, an empty file or a broken
 * JSON file is reported on the console and an empty result is returned instead of
 * crashing the program. The data directory and the JSON file are created
 * automatically the first time they are needed.
 */
public class JsonStorage {

    private final File file;
    private final ObjectMapper mapper;

    public JsonStorage(String filePath) {
        this.file = new File(filePath);
        this.mapper = createMapper();
        ensureFileExists();
    }

    /** Creates the data directory and the JSON file when they do not exist yet. */
    private void ensureFileExists() {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                System.err.println("[JsonStorage] Warning: could not create directory " + parent.getPath());
            }
            if (!file.exists() && !file.createNewFile()) {
                System.err.println("[JsonStorage] Warning: could not create file " + file.getPath());
            }
        } catch (IOException e) {
            System.err.println("[JsonStorage] Warning: " + file.getPath() + " is not writable (" + e.getMessage() + ")");
        }
    }

    private static ObjectMapper createMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /** Reads a JSON array from the file. Returns an empty list when there is no (valid) data. */
    public <T> List<T> loadList(TypeReference<List<T>> type) {
        List<T> empty = new ArrayList<>();
        if (!hasData()) {
            return empty;
        }
        try {
            List<T> loaded = mapper.readValue(file, type);
            return loaded != null ? loaded : empty;
        } catch (Exception e) {
            System.err.println("[JsonStorage] Warning: " + file.getName() + " could not be loaded ("
                    + firstLine(e) + "). Starting with empty data.");
            return empty;
        }
    }

    /** Reads a single JSON object from the file. Returns null when the file is empty or invalid. */
    public <T> T loadObject(TypeReference<T> type) {
        if (!hasData()) {
            return null;
        }
        try {
            return mapper.readValue(file, type);
        } catch (Exception e) {
            System.err.println("[JsonStorage] Warning: " + file.getName() + " could not be loaded ("
                    + firstLine(e) + "). Returning no object.");
            return null;
        }
    }

    /** Writes any Java object (usually a List) to the JSON file. */
    public void save(Object value) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            mapper.writeValue(file, value);
        } catch (IOException e) {
            System.err.println("[JsonStorage] Warning: could not save " + file.getPath()
                    + " (" + firstLine(e) + ")");
        }
    }

    /** Writes a list to the JSON file; null is stored as an empty JSON array. */
    public void saveList(List<?> items) {
        save(items != null ? items : new ArrayList<>());
    }

    /** True when the file exists and contains something that can be parsed. */
    private boolean hasData() {
        return file.exists() && file.length() > 0;
    }

    private static String firstLine(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return e.getClass().getSimpleName();
        }
        int breakAt = message.indexOf('\n');
        return breakAt > 0 ? message.substring(0, breakAt) : message;
    }

    public String getFilePath() {
        return file.getPath();
    }
}