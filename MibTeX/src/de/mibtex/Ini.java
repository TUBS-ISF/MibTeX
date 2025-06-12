package de.mibtex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Ini {
    private final static String ASSIGNMENT_OP = "=";

    private Map<String, String> options;

    private Ini () {
        options = new HashMap<>();
    }

    public void put(String option, String value) {
        options.put(option, value);
    }

    public String get(String option) {
        return options.get(option);
    }

    public static Ini fromLineStream(Stream<String> lines) {
        Ini ini = new Ini();
        lines
            .filter(l -> !l.contains("[options]"))
            .forEach(line -> {
                String[] parts = line.split(ASSIGNMENT_OP, -1);
                if (parts.length == 2) {
                    ini.put(parts[0], parts[1]);
                } else {
                    System.err.println("Ignoring unexpected line \"" + line + "\" in ini file!");
                }
        });

        return ini;
    }

    public static Ini fromFile(Path path) throws IOException {
        try (BufferedReader b = Files.newBufferedReader(path)) {
            final Stream<String> lines = b.lines();
            return fromLineStream(lines);
        }
    }

    public static Ini fromFile(File path) throws IOException {
        return fromFile(path.toPath());
    }

    public static boolean parseBool(String val) {
        return Boolean.parseBoolean(val);
    }
}
