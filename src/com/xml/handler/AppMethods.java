package com.xml.handler;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class AppMethods {

    public void hackTooltipTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            Field fieldDuration = objBehavior.getClass().getDeclaredField("hideTimer");
            fieldDuration.setAccessible(true);
            Timeline objDuration = (Timeline) fieldDuration.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
            objDuration.getKeyFrames().clear();
            objDuration.getKeyFrames().add(new KeyFrame(new Duration(15000)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Alert alertPopup(boolean success, String errorMessage) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(null);
        info.setContentText("It's Done!");

        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(null);
        error.setContentText(errorMessage);

        return success ? info : error;
    }

    public Data convertData(String[] result, String sender, String gln) {
        String orgNr;
        String recName;
        String street;
        String zip;
        String city;
        int n = 0;

        orgNr = result[0].replaceAll("[^\\d]", "").trim();
        recName = result[1].trim();
        if (result.length == 3 || (result.length == 4 && result[2].replaceAll("\\s", "").trim().matches("[0-9]+"))) {
            street = "";
            n = 1;
        } else {
            street = result[2].trim();
        }
        if (result[3 - n].matches(".*[a-zA-Z]+.*")) {
            zip = result[3 - n].replaceAll("[^0-9]", "");
            city = result[3 - n].replaceAll("\\d", "").trim();
        } else {
            zip = result[3 - n].replaceAll("\\s", "");
            city = result[4 - n].trim();
        }

        if (!(orgNr.isEmpty() || recName.isEmpty() || city.isEmpty())) {
            return new Data(sender, gln, orgNr, recName, street, zip, city);
        } else {
            return null;
        }
    }

    public void replaceResource(File file) {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            Path newFile = file.toPath();
            String oldFile = Paths.get(classloader.getResource(newFile.getFileName().toString()).toURI()).toString();
            Path path = Paths.get(oldFile);
            Files.copy(newFile, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}