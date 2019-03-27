package com.github.mozvip.builds.github;

import com.github.mozvip.builds.Artifact;
import com.github.mozvip.builds.BuildProvider;

import java.io.IOException;
import java.time.ZonedDateTime;

public class GithubReleaseBuildProvider implements BuildProvider {

    @Override
    public Artifact retrieveLatestArtifact(ZonedDateTime latestRetrievedDate) throws IOException {
        return null;
    }
}
