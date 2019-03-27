package com.github.mozvip.builds.location;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.mozvip.builds.Artifact;

import java.io.IOException;

@JsonDeserialize(as=LocalFolderLocation.class)
public abstract class InstallationLocation {
    public abstract void importArtifact(Artifact latestArtifact) throws IOException;
}
