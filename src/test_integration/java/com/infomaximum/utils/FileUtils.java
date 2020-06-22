package com.infomaximum.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileUtils {

    private FileUtils() {}

    public static boolean deleteDirectoryIfExists(Path dir) throws IOException {
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return false;
        }

        IOException[] exception = new IOException[]{null};
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null && exception[0] == null) {
                    exception[0] = exc;
                }

                try {
                    Files.delete(dir);
                } catch (IOException e) {
                    if (exception[0] == null) exception[0] = e;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (exception[0] != null) {
            throw exception[0];
        }
        return true;
    }

}
