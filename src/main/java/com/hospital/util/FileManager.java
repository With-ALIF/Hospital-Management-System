package com.hospital.util;

import java.io.File;
import java.io.IOException;

public final class FileManager {
    private FileManager() {
    }

    public static String normalizeDir(String dir) {
        String result = (dir == null || dir.isBlank()) ? "data/" : dir;
        if (!result.endsWith("/") && !result.endsWith("\\")) {
            result = result + "/";
        }
        return result;
    }

    public static void ensureFile(File file) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                System.err.println("[FileManager] Warning: could not create directory " + parent.getPath());
            }
            if (!file.exists() && !file.createNewFile()) {
                System.err.println("[FileManager] Warning: could not create file " + file.getPath());
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Warning: " + file.getPath() + " is not writable (" + e.getMessage() + ")");
        }
    }
}
