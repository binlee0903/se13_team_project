package org.se13.view.ranking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


class RankingScreenControllerTest {
    @Test
    @DisplayName("setArguments 메소드 테스트")
    void setArgumemtsTest() {
        RankingScreenController controller = new RankingScreenController();
        controller.setArguments(100, false, "test");
        assertEquals(100, controller.getScore());
        assertFalse(controller.getIsItem());
        assertEquals("test", controller.getDiff());
    }
}
