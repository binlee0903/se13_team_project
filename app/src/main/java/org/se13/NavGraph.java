package org.se13;

import org.se13.view.nav.Screen;

public interface NavGraph {

    void navigate(Screen screen);

    void popBackStack();
}
