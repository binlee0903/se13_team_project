package org.se13;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.sqlite.config.FakeConfigRepository;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StackNavGraphTest {

    @Test
    @DisplayName("초기에는 스크린이 존재해서는 안됩니다.")
    void initialTest() {
        NavGraph navController = new StackNavGraph(null, new FakeConfigRepository());
        assertThrows(EmptyStackException.class, navController::popBackStack);
    }
}