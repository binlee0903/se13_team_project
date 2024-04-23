package org.se13;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.AppScreen;

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
    public void navigate(AppScreen screen) {
        try {
            navigate(screen, lifecycle -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Lifecycle> void navigate(AppScreen screen, Consumer<T> consumer) {
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

        if (screenWidth!=1920) { // 전체 화면 모드가 아닐 때
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();  // getVisualBounds() 대신 getBounds() 사용
            double centerX = (screenBounds.getWidth() - screenWidth) / 2;
            double centerY = screenBounds.getHeight() / 2 - screenHeight / 2;

            stage.setX(centerX);
            stage.setY(centerY);
        }
        else {
            stage.setX(0);
            stage.setY(0);
        }
    }

    private Scene createScene(FXMLLoader loader) throws IOException {
        return new Scene(loader.load());
    }

    private FXMLLoader createLoader(AppScreen screen) {
        return new FXMLLoader(getClass().getResource(screen.resource));
    }

    private void show(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}
