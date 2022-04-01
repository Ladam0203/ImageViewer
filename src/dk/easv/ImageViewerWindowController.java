package dk.easv;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController {
    private final List<Image> images = new ArrayList<>();
    private int currentImageIndex = 0;
    private Thread slideshowThread;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;

    @FXML
    private void handleBtnLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();
        }
    }

    @FXML
    private void handleBtnPreviousAction() {
        if (!images.isEmpty()) {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction() {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    private void displayImage() {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
        }
    }

    @FXML
    private void handleStartSlideshow() {
        if (!images.isEmpty()) {
            //show dialog to enter how many seconds to wait between images
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Slideshow");
            dialog.setHeaderText("Enter how many seconds to wait between images");
            dialog.setContentText("Seconds:");
            dialog.showAndWait();
            if (dialog.getResult() != null) {
                int seconds = Integer.parseInt(dialog.getResult());
                slideshowThread = new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(seconds * 1000);
                        } catch (InterruptedException ex) {
                            return;
                        }
                        Platform.runLater(this::handleBtnNextAction);
                    }
                });
                slideshowThread.start();
            }
        }
        else
        {
            //pop alert dialog to choose image files
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No images");
            alert.setHeaderText("No images to create slideshow of");
            alert.setContentText("Please choose images to display");
            alert.show();
        }
    }

    @FXML
    private void handleStopSlideshow() {
        slideshowThread.interrupt();
    }
}