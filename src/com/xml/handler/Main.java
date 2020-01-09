package com.xml.handler;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.prefs.Preferences;

import static javafx.scene.layout.GridPane.setHalignment;

public class Main extends Application {

    private static final String USER_DIR = "UserDir";
    private ChangeXML change = new ChangeXML();
    private AppMethods methods = new AppMethods();
    private String exceptionMessage = change.getException();
    private Preferences pref = Preferences.userNodeForPackage(getClass());
    private DirectoryChooser dc = new DirectoryChooser();
    private File dir = new File(pref.get(USER_DIR, System.getProperty("user.dir")));

    private Button startBtn = new Button("Run");
    private Button clearBtn = new Button("Clear");
    private Button exitBtn = new Button("Exit");
    private Button dirBtn = new Button("Dir:");
    private Button openDir = new Button("Open");
    private HBox buttons = new HBox(startBtn, clearBtn, exitBtn);
    private Button both = new Button("FS&TS");
    private CheckBox fs = new CheckBox("FS");
    private CheckBox ts = new CheckBox("TS");
    private HBox checkboxSenders = new HBox(both, fs, ts);
    private Button onTop = new Button("Disabled");

    private Label currentDir = new Label(dir.getAbsolutePath());

    private Text mandatory = new Text(" *");

    private Label senderLabel = new Label("Sender:");
    private Label glnLabel = new Label("GLN:");
    private Label inputLabel = new Label("Organisation number:\n" +
            "Part name:\n" +
            "Street:\n" +
            "Zip code and city:\n" +
            "(City - if not on the same line with zip):");
    private Label statusText = new Label("Status:");
    private Label status = new Label();
    private Label alwaysOnTop = new Label("Always on top:");
    private Label glnInputNumbers = new Label();
    private Label signLabel = new Label("MW");

    private TextField senderOutput = new TextField();
    private TextField glnOutput = new TextField();
    private TextArea inputOutput = new TextArea();

    private TextField glnInput = new TextField();
    private TextArea inputInput = new TextArea();

    private void showData(Data data) {
        String senderSelected = "";
        senderSelected = fs.isSelected() ? senderSelected + "|FS|" : senderSelected;
        senderSelected = ts.isSelected() ? senderSelected + "|TS|" : senderSelected;

        senderOutput.setText(senderSelected);
        glnOutput.setText(data.getRecId());
        inputOutput.setText(data.getRecOrgNr() + "\n" + data.getRecName() + "\n" + data.getRecStreet() + "\n"
                + data.getRecZip() + "\n" + data.getRecCity());
    }

    private void setReceiverEdi(String gln) {
        if (gln.length() == 13) {
            glnInputNumbers.setText("GLN");
            glnInputNumbers.setTextFill(Color.GREEN);
        } else if (gln.length() == 10) {
            glnInputNumbers.setText("Org. nr");
            glnInputNumbers.setTextFill(Color.GREEN);
        } else if (gln.length() == 14
                && gln.substring(0, 2).equals("SE")
                && gln.substring(gln.length() - 2).equals("01")) {
            glnInputNumbers.setText("VAT nr");
            glnInputNumbers.setTextFill(Color.GREEN);
        } else {
            glnInputNumbers.setText("???");
            glnInputNumbers.setTextFill(Color.RED);
        }
    }

    private boolean readInput(String sender) {
        String gln;
        if (sender.isEmpty()) {
            exceptionMessage = "At least one sender has to be checked";
            return false;
        } else {
            String[] result = Arrays.stream(inputInput.getText().split("\n")).filter(line -> !line.isEmpty()).toArray(String[]::new);
            Data data = methods.convertData(result, sender, glnInput.getText());
            gln = glnInput.getText().equals("") ? data.getRecOrgNr() : glnInput.getText().trim();
            data.setRecId(gln);

            showData(data);
            change.change(data, dir);
            setReceiverEdi(gln);

            return true;
        }
    }

    private void clearFields() {
        fs.setSelected(false);
        ts.setSelected(false);
        senderOutput.setText("");
        glnOutput.setText("");
        inputOutput.setText("");
        glnInput.setText("");
        inputInput.setText("");
        glnInputNumbers.setText("");
        status.setText("");
    }

    @Override
    public void start(Stage primaryStage) {
        buttons.setSpacing(5);
        checkboxSenders.setSpacing(5);
        mandatory.setFill(Color.RED);
        onTop.setTextFill(Color.RED);
        signLabel.setTextFill(Color.GRAY);

        senderOutput.setEditable(false);
        glnOutput.setEditable(false);
        inputOutput.setEditable(false);
        senderOutput.setBackground(null);
        glnOutput.setBackground(null);
        inputOutput.setPrefRowCount(5);
        inputOutput.setPrefColumnCount(25);

        inputInput.setPrefRowCount(5);
        inputInput.setPrefColumnCount(25);

        Tooltip senderTooltip = new Tooltip("At least one sender has to be checked");
        Tooltip glnTooltip = new Tooltip("Only if it's provided in pdf file");
        Tooltip inputTooltip = new Tooltip("Copy comment field from ticket");
        Tooltip signTooltip = new Tooltip("Created by WIERZMA1\n" +
                "If you have any questions or suggestions\n" +
                "feel free to contact me:\n" +
                "maciej.wierzchon@opuscapita.com");
        Tooltip currentDirTooltip = new Tooltip(dir.getAbsolutePath());
        methods.hackTooltipTiming(senderTooltip);
        methods.hackTooltipTiming(glnTooltip);
        methods.hackTooltipTiming(inputTooltip);
        methods.hackTooltipTiming(signTooltip);
        methods.hackTooltipTiming(currentDirTooltip);

        senderLabel.setTooltip(senderTooltip);
        glnLabel.setTooltip(glnTooltip);
        inputLabel.setTooltip(inputTooltip);
        signLabel.setTooltip(signTooltip);
        currentDir.setTooltip(currentDirTooltip);
        currentDir.setMaxWidth(260);

        both.setOnAction(e -> {
            if (fs.isSelected() && ts.isSelected()) {
                fs.setSelected(false);
                ts.setSelected(false);
            } else {
                fs.setSelected(true);
                ts.setSelected(true);
            }
        });
        startBtn.setOnAction(e -> {
            if (!fs.isSelected() && !ts.isSelected()) {
                methods.alertPopup(false, "At least one sender has to be checked!");
            } else {
                boolean success = true;
                try {
                    if (fs.isSelected() || ts.isSelected()) {
                        if (fs.isSelected()) {
                            success = readInput("FS");
                        }
                        if (ts.isSelected()) {
                            success = readInput("TS");
                        }
                    }
                } catch (Exception ex) {
                    success = false;
                } finally {
                    if (success) {
                        status.setText("Done");
                        status.setTextFill(Color.GREEN);
                    } else {
                        methods.alertPopup(success, "Something went wrong :(" + "\n" + exceptionMessage).showAndWait();
                        status.setText("Error");
                        status.setTextFill(Color.RED);
                    }
                }
            }
        });
        clearBtn.setOnAction(e -> clearFields());
        exitBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Exit");
            alert.setHeaderText("Exit");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.YES) {
                Platform.exit();
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(0, 10, 0, 10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(senderLabel, 0, 0);
        gridPane.add(glnLabel, 0, 1);
        gridPane.add(inputLabel, 0, 2);
        gridPane.add(buttons, 0, 3);
        gridPane.add(dirBtn, 0, 3);
        setHalignment(dirBtn, HPos.RIGHT);

        gridPane.add(checkboxSenders, 1, 0);
        gridPane.add(glnInput, 1, 1);
        gridPane.add(inputInput, 1, 2);
        gridPane.add(currentDir, 1, 3);
        gridPane.add(openDir, 1, 3);
        setHalignment(openDir, HPos.RIGHT);

        gridPane.add(senderOutput, 2, 0);
        gridPane.add(alwaysOnTop, 2, 0);
        setHalignment(alwaysOnTop, HPos.RIGHT);
        gridPane.add(glnOutput, 2, 1);
        gridPane.add(inputOutput, 2, 2);
        gridPane.add(statusText, 2, 3);
        gridPane.add(status, 2, 3);
        setHalignment(status, HPos.CENTER);

        gridPane.add(onTop, 3, 0);
        gridPane.add(glnInputNumbers, 3, 1);
        gridPane.add(signLabel, 3, 3);
        setHalignment(signLabel, HPos.RIGHT);

        Scene scene = new Scene(gridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Invoice test file creator");
        primaryStage.getIcons().add(new Image(Thread.currentThread().getContextClassLoader().getResourceAsStream("TS.png")));
        primaryStage.show();

        dirBtn.setOnAction(e -> {
            dc.setInitialDirectory(dir);
            File directory = dc.showDialog(primaryStage);
            if (directory != null) {
                dir = directory;
                currentDir.setText(dir.getAbsolutePath());
                currentDirTooltip.setText(dir.getAbsolutePath());
                pref.put(USER_DIR, dir.getAbsolutePath());
            }
        });
        openDir.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(dir.getAbsolutePath()));
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Folder doesn't exist", ButtonType.OK);
                alert.show();
            }
        });
        onTop.setOnAction(e -> {
            if (onTop.getText().equals("Enabled ")) {
                primaryStage.setAlwaysOnTop(false);
                onTop.setText("Disabled");
                onTop.setTextFill(Color.RED);
            } else {
                primaryStage.setAlwaysOnTop(true);
                onTop.setText("Enabled ");
                onTop.setTextFill(Color.GREEN);
            }

        });

        Region region = (Region) inputOutput.lookup(".content");
        region.setBackground(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}