package ru.rkhamatyarov.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Paths.get;

/**
 * The CustomFileManager class provides methods for managing file operations,
 * such as writing content to files.
 */
public class CustomFileManager {

    /**
     * Logger for the CustomFileManager class.
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(CustomFileManager.class);

    /**
     * Singleton instance of CustomFileManager.
     */
    private static final CustomFileManager CUSTOM_FILE_MANAGER
            = new CustomFileManager();

    /**
     * Private constructor to prevent instantiation.
     */
    public CustomFileManager() {
    }

    /**
     * Returns the singleton instance of CustomFileManager.
     *
     * @return the singleton instance of CustomFileManager
     */
    public static CustomFileManager getInstance() {
        return CUSTOM_FILE_MANAGER;
    }

    /**
     * Writes the specified content to a file identified by the given key.
     * If the file already exists, it will be deleted before writing.
     *
     * @param key     the path to the file where content will be written
     * @param content the content to write to the file
     * @return true if the content was successfully written, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public Boolean write(
            final String key,
            final String content
    ) throws IOException {
        Path p = get(key);
        if (Files.exists(p)) {
            Files.delete(p);
        }

        Path path = Files.write(
                p,
                (content + "\n").getBytes(UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
        LOG.info("Content wrote on file {}", path.toAbsolutePath());
        return Files.exists(path);
    }
}
