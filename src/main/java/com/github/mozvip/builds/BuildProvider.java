package com.github.mozvip.builds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.mozvip.builds.appveyor.AppVeyorBuildProvider;
import com.github.mozvip.builds.github.GithubReleaseBuildProvider;

import java.io.IOException;
import java.time.ZonedDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AppVeyorBuildProvider.class, name = "appVeyor"),
        @JsonSubTypes.Type(value = GithubReleaseBuildProvider.class, name = "gitHubRelease")
})
public interface BuildProvider {
    Artifact retrieveLatestArtifact(ZonedDateTime latestRetrievedDate) throws IOException;
}
