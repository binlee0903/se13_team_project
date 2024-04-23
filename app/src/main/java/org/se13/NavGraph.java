package org.se13;

import org.se13.view.lifecycle.Lifecycle;
import org.se13.view.nav.AppScreen;

import java.util.function.Consumer;

public interface NavGraph {

    void navigate(AppScreen screen);

    <T extends Lifecycle> void navigate(AppScreen screen, Consumer<T> consumer);

    void popBackStack();

    void setScreenSize(int[] size);
}
