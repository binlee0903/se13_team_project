package org.se13;

import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.Screen;

import java.util.function.Consumer;

public interface NavGraph {

    void navigate(Screen screen);

    <T extends Lifecycle> void navigate(Screen screen, Consumer<T> consumer);

    void popBackStack();
}
