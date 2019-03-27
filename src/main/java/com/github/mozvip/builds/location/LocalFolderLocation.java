package com.github.mozvip.builds.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mozvip.builds.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFolderLocation extends InstallationLocation {

    public static final Logger LOGGER = LoggerFactory.getLogger(LocalFolderLocation.class);

    private Path folder;

    @JsonCreator
    public LocalFolderLocation(@JsonProperty("folder") Path folder) {
        this.folder = folder;
    }

    public Path getFolder() {
        return folder;
    }

    @Override
    public void importArtifact(Artifact latestArtifact) throws IOException {
        LOGGER.info("Importing artifact {}", latestArtifact);

        Files.createDirectories(folder);

        String file = latestArtifact.getFile().toString();
        if (file.endsWith(".7z") || file.endsWith(".zip")) {

            ProcessBuilder pb =
                    new ProcessBuilder("c:\\Program Files\\7-Zip\\7z.exe", "x", latestArtifact.getFile().toAbsolutePath().toString(), "-o"+folder.toAbsolutePath().toString(), "-y");

            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

        } else {
            Path destinationFile = folder.resolve(latestArtifact.getFile().getFileName());
            Files.move(latestArtifact.getFile(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }


    }

    @Override
    public String toString() {
        return "LocalFolderLocation{" +
                "folder=" + folder +
                '}';
    }
}
