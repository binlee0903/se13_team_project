package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.game.block.Cell;
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

    private int limited = -1000;
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


    private TetrisAction prevAction;

    private void choose(ComputerInput input) {
        if (isEnd) return;
        if (isBattleMode) {
            if (!canChoose.getAndSet(false)) {
                return;
            }
        } else {
            if (limited++ >= fitness / 2) {
                actionRepository.immediateBlockPlace();
                return;
            }

            CellID[][] board = input.tetrisGrid;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[21 - i][j] == CellID.CBLOCK_ID) {
                        limited--;
                        return;
                    }
                }
            }
        }

        TetrisAction choose = neural.predict(input);
        if (choose == TetrisAction.MOVE_BLOCK_LEFT && prevAction == TetrisAction.MOVE_BLOCK_RIGHT) {
            invalidInputEvent();
        }

        if (choose == TetrisAction.MOVE_BLOCK_RIGHT && prevAction == TetrisAction.MOVE_BLOCK_LEFT) {
            invalidInputEvent();
        }

        prevAction = choose;

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
        if (limited >= fitness / 2) return;

        // 라인 클리어 시 가산점
        if (event instanceof LineClearedEvent) {
            lineClearedEvent((LineClearedEvent) event);
        }

        if (event instanceof InvalidInputEvent) {
            invalidInputEvent();
        }

        if (event instanceof NextBlockEvent) {
            nextBlockEvent((NextBlockEvent) event);
        }
    }

    private void lineClearedEvent(LineClearedEvent event) {
        // 1 * 2 * 50 = 100
        // 2 * 3 * 50 = 300
        // 3 * 4 * 50 = 600
        // 4 * 5 * 50 = 1000
        fitness += (event.cleared() + 1) * event.cleared() * 50 + 500;
    }

    private int invalid = 0;

    private void invalidInputEvent() {
        fitness -= invalid++;
    }

    private void nextBlockEvent(NextBlockEvent event) {
        // 오래 살아남을 수록 가산점
        fitness += 10;

        // 빈공간이 많으면 감점
        CellID[][] without = event.withoutCurrentBlock();
        for (int i = 0; i < 10; i++) {
            int count = 0;
            boolean isBlockPlaced = false;
            for (int j = 0; j < 22; j++) {
                if (without[j][i] != CellID.EMPTY) {
                    isBlockPlaced = true;
                } else {
                    if (isBlockPlaced) {
                        count++;
                    }
                }
            }

            fitness -= count * 2;
        }

        // 주변 블럭과 비슷한 높이로 쌓으면 가산점
        int[] tops = new int[10];
        double average = 0;
        for (int i = 0; i < 10; i++) {
            tops[i] = 22;
            for (int j = 0; j < 22; j++) {
                if (without[j][i] != CellID.EMPTY) {
                    tops[i] = j;
                    break;
                }
            }
            average += tops[i];
        }
        average /= 10;

        for (int i = 0; i < 10; i++) {
            double gap = Math.abs(tops[i] - average);
            fitness += (22 - gap) / 4;
        }

        // 가장자리먼저 채울수록 가산점
        int count = 0;
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 10; j++) {
                if (without[i][j] != CellID.EMPTY) {
                    count += Math.abs(j - 5);
                }
            }
        }
        fitness += count / 22;
    }
}
