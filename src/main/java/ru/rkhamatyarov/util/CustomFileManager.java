package ru.rkhamatyarov.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Paths.get;

;

public class CustomFileManager {
    private static final Logger log = LoggerFactory.getLogger(CustomFileManager.class);
    private static final CustomFileManager customFileManager = new CustomFileManager();

    public CustomFileManager() {}

    public static CustomFileManager getInstance() {
        return customFileManager;
    }

    public Boolean write(String key, String content) throws IOException {
        Path p = get(key);
        if (Files.exists(p)) {
            Files.delete(p);
        }

        Path path = Files.write(p, (content + "\n").getBytes(UTF_8), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        log.info("Content wrote on file {}", path.toAbsolutePath().toString());
        return Files.exists(path);
    }
}
