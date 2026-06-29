package com.example.ecommercebackend.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class FrontendUrlConfigurationTest {

    @Test
    void mainSourceShouldNotHardcodeLocalFrontendUrl() throws IOException {
        Path sourceRoot = Path.of("src", "main");

        List<Path> filesWithHardcodedFrontendUrl;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            filesWithHardcodedFrontendUrl = paths
                    .filter(Files::isRegularFile)
                    .filter(this::containsHardcodedFrontendUrl)
                    .toList();
        }

        assertTrue(filesWithHardcodedFrontendUrl.isEmpty(),
                () -> "Use app.frontend.url instead of hardcoding http://localhost:5173: "
                        + filesWithHardcodedFrontendUrl);
    }

    private boolean containsHardcodedFrontendUrl(Path path) {
        try {
            return Files.readString(path).contains("http://localhost:5173");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path, e);
        }
    }
}
