package com.github.mozvip.builds;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.mozvip.builds.appveyor.AppVeyorBuildProvider;
import com.github.mozvip.builds.artifact.Artifact;
import com.github.mozvip.builds.location.LocalFolderLocation;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] argv) throws IOException {

        List<BuildInstallItem> items = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module

        Path configuration = Paths.get("configuration.json");
        if (Files.isReadable(configuration) && Files.isRegularFile(configuration)) {

            items = mapper.readValue(configuration.toFile(), new TypeReference<List<BuildInstallItem>>(){});

        } else {

            BuildInstallItem rpcs3 = new BuildInstallItem(
                    "rpcs3 master",
                    new AppVeyorBuildProvider("rpcs3/rpcs3", "master", null, "rpcs3"),
                    new LocalFolderLocation(Paths.get("d:\\emu\\rpcs3")), null, false
            );

            items.add(rpcs3);

            try {
                mapper.writeValue(configuration.toFile(), items);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boolean downloaded = false;

        for (BuildInstallItem item : items) {
            List<Artifact> artifacts = item.checkAndInstall();
            if (artifacts != null && !artifacts.isEmpty()) {
                String message = item.getLocation() != null ? item.getLocation().toString() : item.getLatestRetrievalDate().toString();
                displayTray("New build for " + item.getName(), message);
                downloaded = true;
            }
        }

        if (!downloaded) {
            displayTray("Nothing new !", "No new downloads were found");
        }

        mapper.writeValue(configuration.toFile(), items);

    }

    public static void displayTray(String title, String message) {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip(message);
        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            tray.remove(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }
}
