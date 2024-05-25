package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.CellID;
import org.se13.game.event.*;
import org.se13.server.TetrisServer;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.view.tetris.Player;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class Computer extends Player {
    private static final Logger log = LoggerFactory.getLogger(Computer.class);

    private int limited = 60;
    private boolean isBattleMode;
    private Neural neural;
    private int fitness = 0;
    private AtomicBoolean canChoose = new AtomicBoolean(true);
    private boolean isEnd;

    private final SaveComputer saver;

    public interface SaveComputer {
        void save(NeuralResult result);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, Neural neural, SaveComputer saver) {
        this(userId, playerKeycode, repository, neural, saver, false);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, Neural neural, SaveComputer saver, boolean isBattleMode) {
        super(userId, playerKeycode, new ComputerEventRepository(repository));
        this.saver = saver;
        this.neural = neural;
        this.isBattleMode = isBattleMode;

        setDelayIfBattleMode();
    }

    @Override
    public void connectToServer(TetrisServer server) {
        super.connectToServer(server);
        ((ComputerEventRepository) eventRepository).subscribe(this::choose);
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
            saver.save(new NeuralResult(fitness, neural));
        }
    }

    private void choose(ComputerInput input) {
        if (isEnd) return;
        if (isBattleMode) {
            if (!canChoose.getAndSet(false)) {
                return;
            }
        } else {
            if (limited-- <= 0) {
                actionRepository.immediateBlockPlace();
                return;
            }
        }

        TetrisAction choose = neural.predict(input);

        switch (choose) {
            case IMMEDIATE_BLOCK_PLACE -> actionRepository.immediateBlockPlace();
            case MOVE_BLOCK_LEFT -> actionRepository.moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> actionRepository.moveBlockRight();
            case ROTATE_BLOCK_CW -> actionRepository.rotateBlockCW();
            default -> throw new IllegalStateException("Unexpected value: " + choose);
        }
    }

    private void onEvent(TetrisEvent event) {
        if (isEnd) return;
        if (limited < 0) return;

        // 라인 클리어 시 가산점
        if (event instanceof LineClearedEvent) {
            lineClearedEvent((LineClearedEvent) event);
        }

        if (event instanceof InvalidInputEvent) {
            invalidInputEvent();
        }

        if (event instanceof NextBlockEvent) {
            nextBlockEvent();
        }

        if (event instanceof UpdateTetrisState) {
            updateTetrisState((UpdateTetrisState) event);
        }
    }

    private void updateTetrisState(UpdateTetrisState event) {
        CellID[][] cells = event.tetrisGrid();

        double count = 0;
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (cells[21 - i][j] != CellID.EMPTY && cells[21 - i][j] != CellID.CBLOCK_ID) {
                    count += Math.abs(j - 5) * 1.2;
                }
            }
        }

        fitness += count / 20;
    }

    private void lineClearedEvent(LineClearedEvent event) {
        fitness += (event.cleared() + 1) * 100;
    }

    private int invalid = 10;
    private void invalidInputEvent() {
        fitness -= invalid++;
    }

    private void nextBlockEvent() {
        fitness++;
    }
}
