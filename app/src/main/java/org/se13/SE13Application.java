package org.se13;

import javafx.application.Application;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.nav.Screen;

public class SE13Application extends Application {
    public static NavGraph navController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        navController = new StackNavGraph(stage, ConfigRepositoryImpl.getInstance());
        navController.navigate(Screen.START);
    }
}
