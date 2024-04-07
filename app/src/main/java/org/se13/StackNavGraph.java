package org.se13;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.Screen;

import java.io.IOException;
import java.util.Stack;
import java.util.function.Consumer;

public class StackNavGraph implements NavGraph {

    private final Stage stage;
    private final Stack<Scene> backStack;
    private final ConfigRepository configRepository;

    public StackNavGraph(Stage stage, ConfigRepository repository) {
        this.stage = stage;
        this.backStack = new Stack<>();
        this.configRepository = repository;

        setScreenSize(configRepository.getScreenSize());
    }

    @Override
    public void navigate(Screen screen) {
        try {
            navigate(screen, lifecycle -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Lifecycle> void navigate(Screen screen, Consumer<T> consumer) {
        try {
            FXMLLoader loader = createLoader(screen);
            Scene scene = createScene(loader);
            consumer
                .andThen(Lifecycle::onCreate)
                .andThen((controller) -> show(backStack.push(scene)))
                .andThen(Lifecycle::onStart)
                .accept(loader.getController());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void popBackStack() {
        backStack.pop();
        show(backStack.lastElement());
    }

    @Override
    public void setScreenSize(int[] size) {
        int screenWidth = size[0];
        int screenHeight = size[1];

        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
    }

    private Scene createScene(FXMLLoader loader) throws IOException {
        return new Scene(loader.load());
    }

    private FXMLLoader createLoader(Screen screen) {
        return new FXMLLoader(getClass().getResource(screen.resource));
    }

    private void show(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}
