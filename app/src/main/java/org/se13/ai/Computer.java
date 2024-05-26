package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.event.LineClearedEvent;
import org.se13.game.event.NextBlockEvent;
import org.se13.game.event.TetrisEvent;
import org.se13.server.TetrisServer;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.view.tetris.Player;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer extends Player {
    private static final Logger log = LoggerFactory.getLogger(Computer.class);

    private int limited = 500;
    int fitness = 0;
    private boolean isBattleMode;
    private Predict predict;
    private AtomicBoolean canChoose = new AtomicBoolean(true);
    private boolean isEnd;
    private Queue<TetrisAction> actions = new ArrayDeque<>();

    private final SaveComputer saver;

    public interface SaveComputer {
        void save(int computerId, NeuralResult result);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, Predict predict, SaveComputer saver) {
        this(userId, playerKeycode, repository, predict, saver, false);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, Predict predict, SaveComputer saver, boolean isBattleMode) {
        super(userId, playerKeycode, new ComputerEventRepository(repository));
        this.saver = saver;
        this.predict = predict;
        this.isBattleMode = isBattleMode;

        setDelayIfBattleMode();
    }

    @Override
    public void connectToServer(TetrisServer server) {
        super.connectToServer(server);
        ((ComputerEventRepository) eventRepository).subscribeEvent(this::onEvent);
        ((ComputerEventRepository) eventRepository).subscribeEnd(this::onEnd);
    }

    private void setDelayIfBattleMode() {
        if (isBattleMode) {
            new Thread(() -> {
                try {
                    while (!isEnd) {
                        canChoose.set(true);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private void onEnd(TetrisGameEndData endData) {
        isEnd = true;

        if (saver != null) {
            saver.save(userId, new NeuralResult(fitness, predict));
        }
    }

    private void choose(ComputerInputEvent input) {
        if (limited <= 0) {
            actionRepository.immediateBlockPlace();
            return;
        }

        if (actions.isEmpty()) {
            actions.addAll(predict.predict(input.board(), input.block()));
        }

        TetrisAction choose = actions.poll();
        if (choose == null) {
            choose = TetrisAction.IMMEDIATE_BLOCK_PLACE;
        }

        switch (choose) {
            case IMMEDIATE_BLOCK_PLACE -> actionRepository.immediateBlockPlace();
            case MOVE_BLOCK_LEFT -> actionRepository.moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> actionRepository.moveBlockRight();
            case ROTATE_BLOCK_CW -> actionRepository.rotateBlockCW();
            case MOVE_BLOCK_DOWN -> actionRepository.moveBlockDown();
            default -> throw new IllegalStateException("Unexpected value: " + choose);
        }
    }

    private void onEvent(TetrisEvent event) {
        if (isEnd) return;

        if (event instanceof ComputerInputEvent) {
            choose((ComputerInputEvent) event);
        }

        // 라인 클리어 시 가산점
        if (event instanceof LineClearedEvent) {
            lineClearedEvent((LineClearedEvent) event);
        }

        if (event instanceof NextBlockEvent) {
            nextBlockEvent();
        }
    }

    private void lineClearedEvent(LineClearedEvent event) {
        fitness += event.cleared();
    }

    void nextBlockEvent() {
        limited--;
    }
}
