package com.github.mozvip.builds.appveyor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.mozvip.builds.BuildProvider;
import com.github.mozvip.builds.artifact.Artifact;
import com.github.mozvip.builds.artifact.HTTPArtifact;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AppVeyorBuildProvider implements BuildProvider {

    public static final Logger LOGGER = LoggerFactory.getLogger(AppVeyorBuildProvider.class);

    private static OkHttpClient client = new OkHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module

    String projectName;
    String branch;
    String deploymentName;
    private String jobRegExp;

    @JsonCreator
    public AppVeyorBuildProvider(@JsonProperty("projectName") String projectName, @JsonProperty("branch") String branch, @JsonProperty("jobRegExp") String jobRegExp, @JsonProperty("deploymentName") String deploymentName) {
        this.projectName = projectName;
        this.branch = branch;
        this.jobRegExp = jobRegExp;
        this.deploymentName = deploymentName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBranch() {
        return branch;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public String getJobRegExp() {
        return jobRegExp;
    }

    @Override
    public List<Artifact> retrieveLatestArtifacts(ZonedDateTime latestRetrievedDate) throws IOException {

        ProjectAndBuild projectAndBuild;

        Request request = new Request.Builder()
                .url(String.format("https://ci.appveyor.com/api/projects/%s/branch/%s", projectName, branch))
                .build();

        try (Response response = client.newCall(request).execute()) {
            projectAndBuild = objectMapper.readValue(response.body().string(), ProjectAndBuild.class);
        }

        if (projectAndBuild.getBuild().getFinished() == null) {
            return null;
        }

        if (latestRetrievedDate != null && !projectAndBuild.getBuild().getFinished().isAfter(latestRetrievedDate)) {
            return null;
        }

        for (Job job : projectAndBuild.getBuild().getJobs()) {
            if ("success".equals(job.getStatus())) {

                if (jobRegExp != null && !Pattern.matches(jobRegExp, job.getName())) {
                    continue;
                }

                Request artifactsRequest = new Request.Builder()
                        .url(String.format("https://ci.appveyor.com/api/buildjobs/%s/artifacts", job.getJobId()))
                        .build();

                try (Response response = client.newCall(artifactsRequest).execute()) {
                    List<AppVeyorArtifact> artifacts = objectMapper.readValue(response.body().string(), new TypeReference<List<AppVeyorArtifact>>() {});

                    for (AppVeyorArtifact artifact : artifacts) {

                        if (latestRetrievedDate != null && !artifact.getCreated().isAfter(latestRetrievedDate)) {
                            return null;
                        }

                        if (deploymentName == null || deploymentName.equals(artifact.getName()) || deploymentName.equals(artifact.getFileName())) {
                            String artifactUrl = String.format("https://ci.appveyor.com/api/buildjobs/%s/artifacts/%s", job.getJobId(), artifact.getFileName());
                            return Arrays.asList(new HTTPArtifact(artifact.getFileName(), artifactUrl, artifact.getCreated()));
                        }

                    }

                }

                break;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "AppVeyorBuildProvider{" +
                "projectName='" + projectName + '\'' +
                ", branch='" + branch + '\'' +
                ", deploymentName='" + deploymentName + '\'' +
                '}';
    }
}
