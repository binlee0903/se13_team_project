package org.se13.view.controller;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.NavGraph;
import org.se13.SE13Application;
import org.se13.game.rule.GameMode;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.difficulty.LevelSelectScreenController;
import org.se13.view.setting.SettingScreenController;

import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerTest extends Application {
    public static NavGraph navController;
    public static Stage stage;

    @BeforeAll
    public static void setUp() {
        Thread thread = new Thread(() -> {
            Application.launch(ControllerTest.class);
        });

        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void start(Stage stage) throws Exception {
        ControllerTest.stage = stage;
    }

    @Test
    @DisplayName("BaseController test")
    void testBaseController() {
        BaseController baseController = new BaseController();
        baseController.onCreate();
        baseController.onStart();
    }

    @Test
    @DisplayName("LevelSelectScreenController test")
    void testLevelSelectScreenController() {
        LevelSelectScreenController levelSelectScreenController = new LevelSelectScreenController();
        levelSelectScreenController.testInit();

        levelSelectScreenController.initialize();

        GameMode gameMode = levelSelectScreenController.setGameMode("default");
        assertEquals(GameMode.DEFAULT, gameMode);

        gameMode = levelSelectScreenController.setGameMode("item");
        assertEquals(GameMode.ITEM, gameMode);

        gameMode = levelSelectScreenController.setGameMode("timeLimit");
        assertEquals(GameMode.TIME_LIMIT, gameMode);
    }

    @Test
    @DisplayName("SettingScreenController test")
    void testSettingScreenController() {
        SettingScreenController settingScreenController = new SettingScreenController();
        settingScreenController.testInit();

        Map<String, String> keySettings = settingScreenController.getKeySettings();
        settingScreenController.handleSettingClearButtonAction();

        assertEquals("a", keySettings.get("keyLeft"));
        assertEquals("d", keySettings.get("keyRight"));
        assertEquals("s", keySettings.get("keyDown"));
        assertEquals("w", keySettings.get("keyDrop"));
        assertEquals("e", keySettings.get("keyRotate"));
        assertEquals("p", keySettings.get("keyPause"));
        assertEquals("q", keySettings.get("keyExit"));
    }
}
