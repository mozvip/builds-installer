package com.github.mozvip.builds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mozvip.builds.artifact.Artifact;
import com.github.mozvip.builds.location.InstallationLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInstallItem {

    private final static Logger LOGGER = LoggerFactory.getLogger(BuildInstallItem.class);

    ZonedDateTime latestRetrievalDate;
    String name;
    BuildProvider provider;
    InstallationLocation location;
    String postInstallCmd;
    boolean suppressParentFolder;

    @JsonCreator
    public BuildInstallItem(@JsonProperty("name") String name, @JsonProperty("provider") BuildProvider provider, @JsonProperty("location") InstallationLocation location, @JsonProperty("postInstallCmd") String postInstallCmd, @JsonProperty("suppressParentFolder") boolean suppressParentFolder) {
        this.name = name;
        this.provider = provider;
        this.location = location;
        this.postInstallCmd = postInstallCmd;
        this.suppressParentFolder = suppressParentFolder;
    }

    public ZonedDateTime getLatestRetrievalDate() {
        return latestRetrievalDate;
    }

    public void setLatestRetrievalDate(ZonedDateTime latestRetrievalDate) {
        this.latestRetrievalDate = latestRetrievalDate;
    }

    public String getName() {
        return name;
    }

    public BuildProvider getProvider() {
        return provider;
    }

    public InstallationLocation getLocation() {
        return location;
    }

    public boolean isSuppressParentFolder() {
        return suppressParentFolder;
    }

    public String getPostInstallCmd() {
        return postInstallCmd;
    }

    public List<Artifact> checkAndInstall() throws IOException {
        List<Artifact> artifacts = provider.retrieveLatestArtifacts(latestRetrievalDate);
        if (artifacts != null && !artifacts.isEmpty()) {
            if (location != null) {
                location.importArtifacts(artifacts);
            }
            if (getPostInstallCmd() != null) {
                for (Artifact artifact : artifacts) {
                    String commandToExecute = postInstallCmd.replace("${ARTIFACT}", artifact.toLocalPath().toAbsolutePath().toString());
                    ProcessBuilder pb = new ProcessBuilder(commandToExecute.split("\\s"));
                    try {
                        Process start = pb.start();
                        start.waitFor();
                    } catch (IOException | InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
            setLatestRetrievalDate(ZonedDateTime.now());
        }
        return artifacts;
    }

    @Override
    public String toString() {
        return "BuildInstallItem{" +
                "latestRetrievalDate=" + latestRetrievalDate +
                ", name='" + name + '\'' +
                ", provider=" + provider +
                ", location=" + location +
                ", postInstallCmd='" + postInstallCmd + '\'' +
                ", suppressParentFolder=" + suppressParentFolder +
                '}';
    }
}
