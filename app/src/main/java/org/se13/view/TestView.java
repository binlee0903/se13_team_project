package org.se13.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TestView {
    public void onButtonClicked() {
        button.setOnMouseClicked(e -> {
            label.setText("Hello!");
        });
    }

    @FXML
    private Label label;

    @FXML
    private Button button;
}
