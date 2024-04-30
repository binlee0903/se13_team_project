package org.se13.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.se13.game.action.TetrisAction;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.view.tetris.TetrisGameEndData;
import org.se13.view.tetris.TetrisState;
import org.se13.view.tetris.TetrisStateRepository;
import org.se13.view.tetris.TetrisStateRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class LocalTetrisServerTest {

    TetrisStateRepository repository;
    TetrisClient client;
    LocalTetrisServer server;

    TetrisState stateTest;
    TetrisGameEndData endDataTest;

    @BeforeEach
    void setUp() {
        repository = new TetrisStateRepositoryImpl() {
            @Override
            public void response(TetrisState state) {
                stateTest = state;
            }

            @Override
            public void gameOver(TetrisGameEndData endData) {
                endDataTest = endData;
            }
        };
        client = new TetrisClient(repository);
        server = new LocalTetrisServer(GameLevel.EASY, GameMode.ITEM, client);
    }

    @Test
    void baseTest() {
        server.handle(TetrisAction.CONNECT);
        server.handle(TetrisAction.TOGGLE_PAUSE_STATE);
        server.handle(TetrisAction.TOGGLE_PAUSE_STATE);
        server.handle(TetrisAction.MOVE_BLOCK_LEFT);
        server.handle(TetrisAction.MOVE_BLOCK_RIGHT);
        server.handle(TetrisAction.ROTATE_BLOCK_CW);
        server.handle(TetrisAction.EXIT_GAME);

        assertNotNull(stateTest);
    }

    @Test
    void gameOverTest() {
        server = new LocalTetrisServer(GameLevel.EASY, GameMode.ITEM, client);
        server.handle(TetrisAction.EXIT_GAME);
        assertEquals("Easy", endDataTest.difficulty());

        server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.ITEM, client);
        server.handle(TetrisAction.EXIT_GAME);
        assertEquals("Normal", endDataTest.difficulty());

        server = new LocalTetrisServer(GameLevel.HARD, GameMode.ITEM, client);
        server.handle(TetrisAction.EXIT_GAME);
        assertEquals("Hard", endDataTest.difficulty());
    }
}