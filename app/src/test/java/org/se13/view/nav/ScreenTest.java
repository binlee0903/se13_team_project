package org.se13.view.nav;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScreenTest {

    @Test
    @DisplayName("Screen 열거형은 .fxml 파일만 지원합니다.")
    void resourceTest() {
        Screen[] screens = Screen.values();

        for (Screen screen : screens) {
            assertTrue(screen.resource.endsWith(".fxml"));
        }
    }
}