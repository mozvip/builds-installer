package com.github.mozvip.builds;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mozvip.builds.location.InstallationLocation;

import java.io.IOException;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInstallItem {

    ZonedDateTime latestRetrievalDate;
    String name;
    BuildProvider provider;
    InstallationLocation location;
    boolean suppressParentFolder;

    @JsonCreator
    public BuildInstallItem(@JsonProperty("name") String name, @JsonProperty("provider") BuildProvider provider, @JsonProperty("location") InstallationLocation location, @JsonProperty("suppressParentFolder") boolean suppressParentFolder) {
        this.name = name;
        this.provider = provider;
        this.location = location;
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

    public Artifact checkAndInstall() throws IOException {
        Artifact latestArtifact = provider.retrieveLatestArtifact(latestRetrievalDate);
        if (latestArtifact != null) {
            setLatestRetrievalDate(latestArtifact.getDateTime());
            location.importArtifact(latestArtifact);
        }
        return latestArtifact;
    }

    @Override
    public String toString() {
        return "BuildInstallItem{" +
                "latestRetrievalDate=" + latestRetrievalDate +
                ", name='" + name + '\'' +
                ", provider=" + provider +
                ", location=" + location +
                ", suppressParentFolder=" + suppressParentFolder +
                '}';
    }
}
