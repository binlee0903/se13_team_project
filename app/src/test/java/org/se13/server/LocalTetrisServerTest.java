package org.se13.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.se13.game.action.TetrisAction;
import org.se13.game.event.TetrisEvent;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.view.tetris.TetrisGameEndData;
import org.se13.game.event.UpdateTetrisState;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisEventRepositoryImpl;

import static org.junit.jupiter.api.Assertions.*;

class LocalTetrisServerTest {

    TetrisEventRepository repository;
    TetrisClient client;
    LocalTetrisServer server;
    TetrisActionHandler handler;

    TetrisEvent stateTest;
    TetrisGameEndData endDataTest;

    @BeforeEach
    void setUp() {
        repository = new TetrisEventRepositoryImpl() {
            @Override
            public void response(TetrisEvent event) {
                stateTest = event;
            }

            @Override
            public void gameOver(TetrisGameEndData endData) {
                endDataTest = endData;
            }
        };
        client = new TetrisClient(-1, repository);
        server = new LocalTetrisServer(GameLevel.EASY, GameMode.ITEM);
        handler = server.connect(client);
    }

    @Test
    void baseTest() throws InterruptedException {
        repository = new TetrisEventRepositoryImpl() {
            private TetrisEvent before = null;

            @Override
            public void response(TetrisEvent event) {
                assertNotSame(before, event);
                before = event;
            }

            @Override
            public void gameOver(TetrisGameEndData endData) {
                endDataTest = endData;
            }
        };
        client = new TetrisClient(-1, repository);
        server = new LocalTetrisServer(GameLevel.EASY, GameMode.ITEM);
        handler = server.connect(client);

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.START));
        server.testPulse();

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.TOGGLE_PAUSE_STATE));
        server.testPulse();

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.TOGGLE_PAUSE_STATE));
        server.testPulse();

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.MOVE_BLOCK_LEFT));
        server.testPulse();

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.MOVE_BLOCK_RIGHT));
        server.testPulse();

        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.ROTATE_BLOCK_CW));
        server.testPulse();
    }

    @Test
    void gameOverTest() {
        server = new LocalTetrisServer(GameLevel.EASY, GameMode.ITEM);
        handler = server.connect(client);
        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.EXIT_GAME));
        assertEquals("Easy", endDataTest.difficulty());

        server = new LocalTetrisServer(GameLevel.NORMAL, GameMode.ITEM);
        handler = server.connect(client);
        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.EXIT_GAME));
        assertEquals("Normal", endDataTest.difficulty());

        server = new LocalTetrisServer(GameLevel.HARD, GameMode.ITEM);
        handler = server.connect(client);
        handler.request(new TetrisActionPacket(client.getUserId(), TetrisAction.EXIT_GAME));
        assertEquals("Hard", endDataTest.difficulty());
    }
}