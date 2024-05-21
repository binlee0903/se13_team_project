package org.se13.ai;

import org.json.JSONObject;
import org.se13.game.action.TetrisAction;
import org.se13.game.block.CellID;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
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
    private double fitness = 0;
    private int layer1 = 10;
    private int layer2 = 20;
    private double[][] w1;
    private double[][] w2;
    private double[][] w3;
    private double[][] w4;

    private boolean isEnd;

    private final SaveComputer saver;

    public interface SaveComputer {
        void save(int computerId, double[][] w1, double[][] w2, double[][] w3, double[][] w4, double fitness);
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
        w1 = JsonUtils.getDoubleArray(content, "w1");
        w2 = JsonUtils.getDoubleArray(content, "w2");
        w3 = JsonUtils.getDoubleArray(content, "w3");
        w4 = JsonUtils.getDoubleArray(content, "w4");
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
        log.info("Computer{} End, Fitness: {}", userId, fitness);
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

    private int previous = 0;

    private void onEvent(TetrisEvent event) {
        if (isEnd) return;
        int count = 0;

        if (event instanceof UpdateTetrisState) {
            CellID[][] cells = ((UpdateTetrisState) event).tetrisGrid();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    if (cells[i][j] != CellID.EMPTY) {
                        count++;
                    }
                }
            }

            int sub = count - previous;
            if (previous < count) {

            } else {
                fitness += sub * sub;
            }

            previous = count;
        }
    }

}
