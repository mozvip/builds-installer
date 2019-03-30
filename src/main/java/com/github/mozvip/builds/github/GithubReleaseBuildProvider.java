package com.github.mozvip.builds.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mozvip.builds.artifact.Artifact;
import com.github.mozvip.builds.BuildProvider;
import com.github.mozvip.builds.artifact.HTTPArtifact;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class GithubReleaseBuildProvider implements BuildProvider {

    private String projectName;
    private String assetNameRegExp;

    @JsonCreator
    public GithubReleaseBuildProvider(@JsonProperty("projectName") String projectName, @JsonProperty("assetNameRegExp") String assetNameRegExp) {
        this.projectName = projectName;
        this.assetNameRegExp = assetNameRegExp;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getAssetNameRegExp() {
        return assetNameRegExp;
    }

    @Override
    public List<Artifact> retrieveLatestArtifacts(ZonedDateTime latestRetrievedDate) throws IOException {

        GitHub github = GitHub.connect();
        GHRepository repository = github.getRepository(this.projectName);

        GHRelease latestRelease = repository.getLatestRelease();
        Date published_at = latestRelease.getPublished_at();

        ZonedDateTime publishedAt = published_at.toInstant().atZone(ZoneId.systemDefault());
        if (latestRetrievedDate == null || latestRetrievedDate.isBefore(publishedAt)) {
            List<Artifact> artifacts = new ArrayList<>();
            List<GHAsset> assets = latestRelease.getAssets();
            for (GHAsset asset : assets) {
                if (assetNameRegExp == null || Pattern.matches(assetNameRegExp, asset.getName())) {
                    HTTPArtifact httpArtifact = new HTTPArtifact(asset.getName(), asset.getBrowserDownloadUrl(), publishedAt);
                    artifacts.add(httpArtifact);
                }
            }
            return artifacts;
        }
        return null;
    }
}
