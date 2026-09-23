package com.hospital.util;

import java.util.List;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String next(List<String> existingIds, String prefix, int firstNumber) {
        int highest = firstNumber - 1;
        for (String id : existingIds) {
            if (id == null || !id.startsWith(prefix)) {
                continue;
            }
            try {
                highest = Math.max(highest, Integer.parseInt(id.substring(prefix.length()).trim()));
            } catch (NumberFormatException ignored) {
            }
        }
        return format(prefix, highest + 1);
    }

    public static String format(String prefix, int number) {
        return prefix + String.format("%04d", number);
    }
}
