package com.github.mozvip.builds.appveyor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectAndBuild {

    Project project;
    Build build;

    public Project getProject() {
        return project;
    }

    public Build getBuild() {
        return build;
    }

    @JsonCreator
    public ProjectAndBuild(@JsonProperty("project") Project project, @JsonProperty("build") Build build) {
        this.project = project;
        this.build = build;
    }

    @Override
    public String toString() {
        return "ProjectAndBuild{" +
                "project=" + project +
                ", build=" + build +
                '}';
    }
}
