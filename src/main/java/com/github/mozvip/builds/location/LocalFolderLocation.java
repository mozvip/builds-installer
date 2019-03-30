package com.github.mozvip.builds.location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mozvip.builds.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalFolderLocation extends InstallationLocation {

    public static final Logger LOGGER = LoggerFactory.getLogger(LocalFolderLocation.class);
    public static final String _7_ZIP_EXE = "c:\\Program Files\\7-Zip\\7z.exe";

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
        Files.createDirectories(folder);

        Path localPath = latestArtifact.toLocalPath();

        String file = localPath.toString();
        if (file.endsWith(".7z") || file.endsWith(".zip")) {

            ProcessBuilder pb =
                    new ProcessBuilder(_7_ZIP_EXE, "x", localPath.toAbsolutePath().toString(), "-o"+folder.toAbsolutePath().toString(), "-y");

            Process p = pb.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

        } else {
            Path destinationFile = folder.resolve(localPath.getFileName());
            Files.move(localPath, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @Override
    public String toString() {
        return folder.toAbsolutePath().toString();
    }
}
