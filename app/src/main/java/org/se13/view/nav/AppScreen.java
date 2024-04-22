package org.se13.view.nav;

public enum AppScreen {
    START("StartScreen.fxml"),
    LEVEL_SELECT("LevelSelectScreen.fxml"),
    TETRIS("TetrisScreen.fxml"),
    SETTING("SettingScreen.fxml"),
    RANKING("RankingScreen.fxml"),
    GAMEOVER("GameOverScreen.fxml");

    AppScreen(String fxml) {
        resource = fxml;
    }

    public final String resource;
}
