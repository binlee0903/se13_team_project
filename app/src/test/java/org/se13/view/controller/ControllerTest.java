package org.se13.view.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.NavGraph;
import org.se13.SE13Application;
import org.se13.sqlite.config.ConfigRepository;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.base.BaseController;
import org.se13.view.setting.SettingScreenController;

public class ControllerTest {
    public static NavGraph navController;

    @Test
    @DisplayName("BaseController test")
    void testBaseController() {
        BaseController baseController = new BaseController();
        baseController.onCreate();
        baseController.onStart();
    }
}
