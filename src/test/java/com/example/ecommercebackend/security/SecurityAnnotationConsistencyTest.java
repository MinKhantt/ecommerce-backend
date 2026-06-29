package com.example.ecommercebackend.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class SecurityAnnotationConsistencyTest {

    @Test
    void preAuthorizeHasRoleShouldNotIncludeRolePrefix() throws IOException {
        Path sourceRoot = Path.of("src", "main", "java", "com", "example", "ecommercebackend");

        List<Path> filesWithPrefixedHasRole;
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            filesWithPrefixedHasRole = paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .filter(this::containsPrefixedHasRole)
                    .toList();
        }

        assertTrue(filesWithPrefixedHasRole.isEmpty(),
                () -> "Use hasRole('ADMIN'), not hasRole('ROLE_ADMIN'): " + filesWithPrefixedHasRole);
    }

    private boolean containsPrefixedHasRole(Path path) {
        try {
            return Files.readString(path).contains("hasRole('ROLE_");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path, e);
        }
    }
}
