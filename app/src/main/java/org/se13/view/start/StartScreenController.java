package org.se13.view.start;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.json.JSONObject;
import org.se13.SE13Application;
import org.se13.ai.Computer;
import org.se13.ai.ComputerEventRepository;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.server.LocalTetrisServer;
import org.se13.utils.JsonUtils;
import org.se13.utils.Matrix;
import org.se13.view.base.BaseController;
import org.se13.view.nav.AppScreen;
import org.se13.view.tetris.TetrisEventRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class StartScreenController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(StartScreenController.class);
    @FXML
    private Button startButton;
    @FXML
    public Button trainingButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button scoreButton;
    @FXML
    private Button quitButton;

    @FXML
    private void handleTetrisButtonAction() {
        SE13Application.navController.navigate(AppScreen.LEVEL_SELECT);
    }

    @FXML
    private void handleSettingsButtonAction() {
        // Turn into a setting screen
        SE13Application.navController.navigate(AppScreen.SETTING);
    }

    @FXML
    private void handleTrainingButtonAction() {
        SE13Application.navController.navigate(AppScreen.TRAINING);
    }

    @FXML
    private void handleScoreButtonAction() {
        // Turn into a scoreboard screen
        SE13Application.navController.navigate(AppScreen.RANKING);
    }

    @FXML
    private void handleQuitButtonAction() {
        // Quit the app
        System.exit(0);
    }
}
