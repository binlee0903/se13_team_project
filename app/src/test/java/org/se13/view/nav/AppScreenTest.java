package org.se13.view.nav;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppScreenTest {

    @Test
    @DisplayName("Screen 열거형은 .fxml 파일만 지원합니다.")
    void resourceTest() {
        AppScreen[] screens = AppScreen.values();

        for (AppScreen screen : screens) {
            assertTrue(screen.resource.endsWith(".fxml"));
        }
    }
}