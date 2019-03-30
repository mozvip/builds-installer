package com.github.mozvip.builds.location;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.mozvip.builds.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LocalFolderLocation.class, name = "local"),
        @JsonSubTypes.Type(value = SSHFolderLocation.class, name = "ssh")
})
public abstract class InstallationLocation {

    public static final Logger LOGGER = LoggerFactory.getLogger(LocalFolderLocation.class);

    public void importArtifacts(List<Artifact> artifacts) throws IOException {
        for (Artifact artifact : artifacts) {
            LOGGER.info("Importing artifact {}", artifact);

            importArtifact(artifact);
        }
    }

    protected abstract void importArtifact(Artifact artifact) throws IOException;
}
