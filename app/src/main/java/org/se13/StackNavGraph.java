package org.se13;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.Screen;

import java.io.IOException;
import java.util.Stack;
import java.util.function.Consumer;

public class StackNavGraph implements NavGraph {

    private final Stage stage;
    private final Stack<Scene> backStack;
    private int screenWidth = 300; // 초기 화면 너비 기본값
    private int screenHeight = 400; // 초기 화면 높이 기본값
    private ConfigRepositoryImpl configRepository;

    public StackNavGraph(Stage stage) {
        this.stage = stage;
        this.backStack = new Stack<>();
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

    private Scene createScene(Screen screen) throws IOException {
        FXMLLoader loader = createLoader(screen);
        return createScene(loader);
    }

    private Scene createScene(FXMLLoader loader) throws IOException {
        configRepository.getConfig(100);
        return new Scene(loader.load(), screenWidth,  screenHeight);
    }

    private FXMLLoader createLoader(Screen screen) {
        return new FXMLLoader(getClass().getResource(screen.resource));
    }

    private void show(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }

}
