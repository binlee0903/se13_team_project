package org.se13;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.se13.view.nav.Screen;

import java.io.IOException;
import java.util.Stack;
import java.util.function.Consumer;

public class StackNavGraph implements NavGraph {

    private final Stage stage;
    private final Stack<Scene> backStack;

    public StackNavGraph(Stage stage) {
        this.stage = stage;
        this.backStack = new Stack<>();
    }

    @Override
    public void navigate(Screen screen) {
        try {
            Scene scene = createScene(screen);
            show(backStack.push(scene));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void navigate(Screen screen, Consumer<T> consumer) {
        try {
            FXMLLoader loader = createLoader(screen);
            Scene scene = createScene(loader);
            consumer.accept(loader.getController());
            show(backStack.push(scene));
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
        return new Scene(loader.load(), 300,  400);
    }

    private FXMLLoader createLoader(Screen screen) {
        return new FXMLLoader(getClass().getResource(screen.resource));
    }

    private void show(Scene scene) {
        stage.setScene(scene);
        stage.show();
    }
}
