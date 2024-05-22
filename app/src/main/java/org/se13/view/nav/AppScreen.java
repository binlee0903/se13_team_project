package org.se13.view.nav;

public enum AppScreen {
    START("StartScreen.fxml"),
    LEVEL_SELECT("LevelSelectScreen.fxml"),
    TETRIS("TetrisScreen.fxml"),
    BATTLE("BattleScreen.fxml"),
    SETTING("SettingScreen.fxml"),
    RANKING("RankingScreen.fxml"),
    GAMEOVER("GameOverScreen.fxml"),
    TRAINING("TrainingScreenController.fxml");

    AppScreen(String fxml) {
        resource = fxml;
    }

    public final String resource;
}
