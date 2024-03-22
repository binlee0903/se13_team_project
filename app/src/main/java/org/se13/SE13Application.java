package org.se13;

import javafx.application.Application;
import javafx.stage.Stage;
import org.se13.view.nav.Screen;

import java.io.IOException;

public class SE13Application extends Application {
    public static NavGraph navController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        navController = new StackNavGraph(stage);
        navController.navigate(Screen.START);
    }
}
