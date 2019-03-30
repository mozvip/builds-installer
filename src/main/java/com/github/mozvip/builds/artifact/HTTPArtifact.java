package com.github.mozvip.builds.artifact;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

public class HTTPArtifact extends Artifact{

    private static OkHttpClient client = new OkHttpClient();

    private String url;

    private Path localPath = null;

    public HTTPArtifact(String name, String url, ZonedDateTime dateTime) {
        super(name, dateTime);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public Path toLocalPath() throws IOException {

        if (localPath != null) {
            return localPath;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        String folderName = getName().replaceAll("[\\\\\\./]", "_");

        localPath = Files.createTempDirectory(folderName).resolve(getName());
        try (Response response = client.newCall(request).execute()) {
            Files.createDirectories(localPath.getParent());
            Files.copy(response.body().byteStream(), localPath);
        }
        return localPath;
    }

    @Override
    public String toString() {
        return url;
    }
}
