package org.se13.view.controller;

import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.NavGraph;
import org.se13.game.block.Block;
import org.se13.game.block.CellID;
import org.se13.game.config.Config;
import org.se13.game.rule.GameMode;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.difficulty.LevelSelectScreenController;
import org.se13.view.setting.SettingScreenController;
import org.se13.view.tetris.TetrisScreenController;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        settingScreenController.onCreate();

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

    @Test
    @DisplayName("TetrisScreenController Test")
    void testTetrisScreenController() {
        TetrisScreenController tetrisScreenController = new TetrisScreenController();
        tetrisScreenController.setTestMode(stage);

        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.ATTACKED_BLOCK_ID));
        tetrisScreenController.handleKeyEvent("p");

        assertEquals(Color.rgb(100, 100, 100), tetrisScreenController.getCellColor(CellID.ATTACKED_BLOCK_ID));
        assertEquals(Color.rgb(0, 0, 255), tetrisScreenController.getCellColor(CellID.IBLOCK_ID));
        assertEquals(Color.rgb(255, 0, 0), tetrisScreenController.getCellColor(CellID.JBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 0), tetrisScreenController.getCellColor(CellID.OBLOCK_ID));
        assertEquals(Color.rgb(0, 255, 0), tetrisScreenController.getCellColor(CellID.LBLOCK_ID));
        assertEquals(Color.rgb(255, 165, 0), tetrisScreenController.getCellColor(CellID.SBLOCK_ID));
        assertEquals(Color.rgb(135, 206, 235), tetrisScreenController.getCellColor(CellID.TBLOCK_ID));
        assertEquals(Color.rgb(128, 0, 128), tetrisScreenController.getCellColor(CellID.ZBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 255), tetrisScreenController.getCellColor(CellID.WEIGHT_BLOCK_ID));
        assertNull(tetrisScreenController.getCellColor(CellID.EMPTY));

        ConfigRepository configRepository = new ConfigRepositoryImpl(0);
        Map<String, Object> configs = configRepository.getConfig();

        String oldValue = (String) configs.get("mode");
        configs.put("mode", "Red-green");

        configRepository.updateConfig(
                (String) configs.get("mode"),
                (int) configs.get("screenWidth"),
                (int) configs.get("screenHeight"),
                (String) configs.get("keyLeft"),
                (String) configs.get("keyRight"),
                (String) configs.get("keyDown"),
                (String) configs.get("keyRotate"),
                (String) configs.get("keyPause"),
                (String) configs.get("keyDrop"),
                (String) configs.get("keyExit")
        );

        Block.reInit();

        assertEquals(Color.rgb(100, 100, 100), tetrisScreenController.getCellColor(CellID.ATTACKED_BLOCK_ID));
        assertEquals(Color.rgb(0, 0, 255), tetrisScreenController.getCellColor(CellID.IBLOCK_ID));
        assertEquals(Color.rgb(255, 192, 203), tetrisScreenController.getCellColor(CellID.JBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 0), tetrisScreenController.getCellColor(CellID.OBLOCK_ID));
        assertEquals(Color.rgb(0, 128, 128), tetrisScreenController.getCellColor(CellID.LBLOCK_ID));
        assertEquals(Color.rgb(128, 0, 128), tetrisScreenController.getCellColor(CellID.SBLOCK_ID));
        assertEquals(Color.rgb(173, 216, 230), tetrisScreenController.getCellColor(CellID.TBLOCK_ID));
        assertEquals(Color.rgb(255, 200, 100), tetrisScreenController.getCellColor(CellID.ZBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 255), tetrisScreenController.getCellColor(CellID.WEIGHT_BLOCK_ID));
        assertNull(tetrisScreenController.getCellColor(CellID.EMPTY));

        configs.put("mode", "Blue-yellow");

        configRepository.updateConfig(
                (String) configs.get("mode"),
                (int) configs.get("screenWidth"),
                (int) configs.get("screenHeight"),
                (String) configs.get("keyLeft"),
                (String) configs.get("keyRight"),
                (String) configs.get("keyDown"),
                (String) configs.get("keyRotate"),
                (String) configs.get("keyPause"),
                (String) configs.get("keyDrop"),
                (String) configs.get("keyExit")
        );

        Block.reInit();

        assertEquals(Color.rgb(100, 100, 100), tetrisScreenController.getCellColor(CellID.ATTACKED_BLOCK_ID));
        assertEquals(Color.rgb(255, 0, 0), tetrisScreenController.getCellColor(CellID.IBLOCK_ID));
        assertEquals(Color.rgb(0, 255, 0), tetrisScreenController.getCellColor(CellID.JBLOCK_ID));
        assertEquals(Color.rgb(135, 206, 235), tetrisScreenController.getCellColor(CellID.OBLOCK_ID));
        assertEquals(Color.rgb(128, 0, 128), tetrisScreenController.getCellColor(CellID.LBLOCK_ID));
        assertEquals(Color.rgb(255, 165, 0), tetrisScreenController.getCellColor(CellID.SBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 224), tetrisScreenController.getCellColor(CellID.TBLOCK_ID));
        assertEquals(Color.rgb(192, 192, 192), tetrisScreenController.getCellColor(CellID.ZBLOCK_ID));
        assertEquals(Color.rgb(255, 255, 255), tetrisScreenController.getCellColor(CellID.WEIGHT_BLOCK_ID));
        assertNull(tetrisScreenController.getCellColor(CellID.EMPTY));

        configs.put("mode", oldValue);
        configRepository.updateConfig(
                (String) configs.get("mode"),
                (int) configs.get("screenWidth"),
                (int) configs.get("screenHeight"),
                (String) configs.get("keyLeft"),
                (String) configs.get("keyRight"),
                (String) configs.get("keyDown"),
                (String) configs.get("keyRotate"),
                (String) configs.get("keyPause"),
                (String) configs.get("keyDrop"),
                (String) configs.get("keyExit")
        );

        assertEquals(' ', tetrisScreenController.getCellCharacter(CellID.EMPTY));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.IBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.JBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.LBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.OBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.SBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.TBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.ZBLOCK_ID));
        assertEquals('0', tetrisScreenController.getCellCharacter(CellID.CBLOCK_ID));
        assertEquals('F', tetrisScreenController.getCellCharacter(CellID.FEVER_ITEM_ID));
        assertEquals('W', tetrisScreenController.getCellCharacter(CellID.WEIGHT_ITEM_ID));
        assertEquals('W', tetrisScreenController.getCellCharacter(CellID.WEIGHT_BLOCK_ID));
        assertEquals('R', tetrisScreenController.getCellCharacter(CellID.RESET_ITEM_ID));
        assertEquals('L', tetrisScreenController.getCellCharacter(CellID.LINE_CLEAR_ITEM_ID));
        assertEquals('A', tetrisScreenController.getCellCharacter(CellID.ALL_CLEAR_ITEM_ID));
    }

    @Test
    @DisplayName("TetrisScreenController Color Test")
    void testTetrisScreenControllerColorTest() {

    }
}
