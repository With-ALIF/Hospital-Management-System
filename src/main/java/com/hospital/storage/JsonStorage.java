package com.hospital.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.util.FileManager;
import com.hospital.util.JsonManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private final File file;
    private final ObjectMapper mapper;

    public JsonStorage(String filePath) {
        this.file = new File(filePath);
        this.mapper = JsonManager.mapper();
        FileManager.ensureFile(file);
    }

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

    public void saveList(List<?> items) {
        save(items != null ? items : new ArrayList<>());
    }

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
