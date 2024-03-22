package org.se13.view.nav;

public enum Screen {
    START("StartScreen.fxml"),
    TETRIS("TetrisScreen.fxml"),
    SETTING("SettingScreen.fxml"),
    RANKING("RankingScreen.fxml");

    Screen(String fxml) {
        resource = fxml;
    }

    public final String resource;
}
