package org.se13.ai;

import org.json.JSONObject;
import org.se13.game.action.TetrisAction;
import org.se13.game.block.CellID;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.game.grid.TetrisGrid;
import org.se13.server.TetrisServer;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.utils.JsonUtils;
import org.se13.utils.Matrix;
import org.se13.view.tetris.Player;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;

import java.util.Arrays;

public class Computer extends Player {
    private TetrisAction[] available = new TetrisAction[]{TetrisAction.MOVE_BLOCK_LEFT, TetrisAction.MOVE_BLOCK_RIGHT, TetrisAction.IMMEDIATE_BLOCK_PLACE, TetrisAction.ROTATE_BLOCK_CW};
    private long fitness = 0;
    private int layer1 = 10;
    private int layer2 = 20;
    private float[][] w1;
    private float[][] w2;
    private float[][] w3;
    private float[][] w4;

    private boolean canChoose = true;
    private long delay;
    private boolean isTraining;
    private boolean isEnd;

    private long isTrainingEndScore = -10000;
    private long isNotImmediate = -8;
    private CellID[][] previousBoard = new TetrisGrid(22, 10).getGrid();
    private long previousCleared = 0;
    private long previousCount = 0;
    private long previousFilled = 0;
    private TetrisAction currentAction;
    private TetrisAction previousAction;

    private final SaveComputer saver;

    public interface SaveComputer {
        void save(int computerId, float[][] w1, float[][] w2, float[][] w3, float[][] w4, float fitness);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, JSONObject content, SaveComputer saver) {
        this(userId, playerKeycode, repository, content, saver, 0);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, JSONObject content, SaveComputer saver, long delay) {
        super(userId, playerKeycode, new ComputerEventRepository(repository));
        this.saver = saver;
        this.delay = delay;
        this.isTraining = delay == 0;

        w1 = Matrix.randn(22, layer1);
        w2 = Matrix.randn(layer1, layer2);
        w3 = Matrix.randn(layer2, layer1);
        w4 = Matrix.randn(layer1, 4);

        if (content != null) {
            load(content);
        }

        if (delay != 0) {
            new Thread(() -> {
                try {
                    while (true) {
                        canChoose = true;
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    private void load(JSONObject content) {
        w1 = JsonUtils.getFloatArray(content, "w1");
        w2 = JsonUtils.getFloatArray(content, "w2");
        w3 = JsonUtils.getFloatArray(content, "w3");
        w4 = JsonUtils.getFloatArray(content, "w4");
    }

    @Override
    public void connectToServer(TetrisServer server) {
        super.connectToServer(server);
        ((ComputerEventRepository) eventRepository).subscribe(this::choose);
        ((ComputerEventRepository) eventRepository).subscribeEvent(this::onEvent);
        ((ComputerEventRepository) eventRepository).subscribeEnd(this::onEnd);
    }

    private void onEnd(TetrisGameEndData endData) {
        isEnd = true;
        // 죽었을 때 블럭이 많이 남아있을 수록 가산점
        fitness += previousCount * previousCount;
        saver.save(userId, w1, w2, w3, w4, fitness);
    }


    private void choose(ComputerInput input) {
        // 만약 점수가 너무 낮으면 종료시키기
        if (fitness < isTrainingEndScore) {
            actionRepository.immediateBlockPlace();
            return;
        }
        if (!canChoose) return;
        if (!isTraining) {
            canChoose = false;
        }
        if (isEnd) return;
        int choose = input.inputs(w1, w2, w3, w4);

        // 블록 드롭을 하지 않으면 감점 (5개까지 다른 동작을 하면 가산점)
        isNotImmediate++;
        if (isNotImmediate > 0) { // 동작을 정하지 못하고 있으면 강제 제거
            fitness += isTrainingEndScore;
        }

        if (available[choose] == TetrisAction.IMMEDIATE_BLOCK_PLACE) {
            isNotImmediate = -8;
        }

        switch (available[choose]) {
            case IMMEDIATE_BLOCK_PLACE -> actionRepository.immediateBlockPlace();
            case MOVE_BLOCK_LEFT -> actionRepository.moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> actionRepository.moveBlockRight();
            case ROTATE_BLOCK_CW -> actionRepository.rotateBlockCW();
            default -> throw new IllegalStateException("Unexpected value: " + available[choose]);
        }

        currentAction = available[choose];
    }


    private void onEvent(TetrisEvent event) {
        if (isEnd) return;
        long count = 0;
        long cleared = 0;
        long filled = 0;

        if (event instanceof UpdateTetrisState) {
            CellID[][] cells = ((UpdateTetrisState) event).tetrisGrid();
            for (CellID[] cell : cells) {
                long lineFilled = 0;

                for (int i = 0; i < cell.length; i++) {
                    // 클리어 시킨 블럭 개수 체크
                    if (cell[i] == CellID.CBLOCK_ID) {
                        cleared++;
                    }

                    if (cell[i] != CellID.EMPTY) {
                        count++;
                    }

                    if (cell[i] != CellID.EMPTY && cell[i] != CellID.CBLOCK_ID) {
                        lineFilled++;
                    }
                }

                // lineFilled가 0이라면 가중치는 없을거고, 1이라면 2, 9라면 90으로 더 가중해서 들어가게 됨.
                long multiple = lineFilled + 1;
                filled += lineFilled * multiple;
            }

            if (cleared > 0 && previousAction == null) {
                previousAction = currentAction;
            }

            // 블록이 변경됐을 때
            if (!Arrays.deepEquals(cells, previousBoard)) {
                // 블록이 제거되었을 때
                if (previousCleared > 0) {
                    if (previousAction == TetrisAction.ROTATE_BLOCK_CW) {
                        fitness += 100;
                    }
                }
                fitness++; // 오래 버틸수록 가산점
                fitness += previousFilled; // 라인을 촘촘하게 채웠을 수록 가산점

                // 제거한 블럭 가산점, 증폭해서 넣어줌
                long multiplier = previousCleared + 1;
                fitness += previousCleared * multiplier * 1000;
            }

            previousBoard = cells;
            previousCleared = cleared;
            previousCount = count;
            previousFilled = filled;
        }
    }
}
