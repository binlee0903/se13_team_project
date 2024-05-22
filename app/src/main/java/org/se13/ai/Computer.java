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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Computer extends Player {
    private static final Logger log = LoggerFactory.getLogger(Computer.class);

    private TetrisAction[] available = new TetrisAction[]{TetrisAction.MOVE_BLOCK_LEFT, TetrisAction.MOVE_BLOCK_RIGHT, TetrisAction.ROTATE_BLOCK_CW, TetrisAction.IMMEDIATE_BLOCK_PLACE};
    private long fitness = 0;
    private int layer1 = 10;
    private int layer2 = 20;
    private float[][] w1;
    private float[][] w2;
    private float[][] w3;
    private float[][] w4;

    private boolean isEnd;

    private final SaveComputer saver;

    public interface SaveComputer {
        void save(int computerId, float[][] w1, float[][] w2, float[][] w3, float[][] w4, float fitness);
    }

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository, JSONObject content, SaveComputer saver) {
        super(userId, playerKeycode, new ComputerEventRepository(repository));
        this.saver = saver;

        w1 = Matrix.randn(10, layer1);
        w2 = Matrix.randn(layer1, layer2);
        w3 = Matrix.randn(layer2, layer1);
        w4 = Matrix.randn(layer1, 4);

        if (content != null) {
            load(content);
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

        new Thread(() -> {
            long timeOut = 500;
            while (!isEnd) {
                try {
                    Thread.sleep(timeOut);
                    actionRepository.immediateBlockPlace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
    }

    private void onEnd(TetrisGameEndData endData) {
        isEnd = true;
        fitness += endData.score() * 100L; // 점수 가산점
        log.info("Computer{} End, Fitness: {}, w1: {}", userId, fitness, w1);
        saver.save(userId, w1, w2, w3, w4, fitness);
    }

    private void choose(ComputerInput input) {
        if (isEnd) return;
        int choose = input.inputs(w1, w2, w3, w4);

        switch (available[choose]) {
            case IMMEDIATE_BLOCK_PLACE -> actionRepository.immediateBlockPlace();
            case MOVE_BLOCK_LEFT -> actionRepository.moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> actionRepository.moveBlockRight();
            case ROTATE_BLOCK_CW -> actionRepository.rotateBlockCW();
        }
    }

    private CellID[][] previousBoard = new TetrisGrid(22, 10).getGrid();
    private int previous = 0;

    private void onEvent(TetrisEvent event) {
        if (isEnd) return;
        int count = 0;
        int notSame = 0;

        if (event instanceof UpdateTetrisState) {
            CellID[][] cells = ((UpdateTetrisState) event).tetrisGrid();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j] != CellID.EMPTY) {
                        count++;
                    }

                    if (previousBoard[i][j] != cells[i][j]) {
                        notSame++;
                    }
                }
            }

            int sub = count - previous;
            fitness++; // 오래 버틸수록 가산점

            if (count > previous) {
                fitness += (long) sub * sub * sub; // 블럭을 제거할 경우 가산점
            }

            if (notSame == 0) {
                fitness -= 10L; // 무동작을 계속할 경우 감점
            }

            previousBoard = cells;
            previous = count;
        }
    }
}
