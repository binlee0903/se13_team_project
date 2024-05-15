package org.se13;

import javafx.application.Application;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.nav.AppScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SE13Application extends Application {
    private static final Logger log = LoggerFactory.getLogger(SE13Application.class);
    public static NavGraph navController;
    public static boolean isTestMode = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        if (isTestMode != true) {
            stage.setResizable(false);
            navController = new StackNavGraph(stage, new ConfigRepositoryImpl(0));
            navController.navigate(AppScreen.START);
        }
    }
}
