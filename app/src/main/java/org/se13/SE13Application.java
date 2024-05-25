package org.se13;

import javafx.application.Application;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.utils.JsonUtils;
import org.se13.view.nav.AppScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SE13Application extends Application {
    private static final Logger log = LoggerFactory.getLogger(SE13Application.class);
    public static NavGraph navController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setResizable(false);
        navController = new StackNavGraph(stage, new ConfigRepositoryImpl(0));
        navController.navigate(AppScreen.START);

        File computer = new File(JsonUtils.path);
        if (!computer.exists()) {
            computer.createNewFile();
            try (FileWriter writer = new FileWriter(computer)) {
                writer.write("{\n}");
                writer.flush();
            }
        }
    }
}
