package org.se13.ai;

import org.se13.game.action.TetrisAction;
import org.se13.server.TetrisServer;
import org.se13.sqlite.config.PlayerKeycode;
import org.se13.utils.Matrix;
import org.se13.view.tetris.Player;
import org.se13.view.tetris.TetrisEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Computer extends Player {
    private static final Logger log = LoggerFactory.getLogger(Computer.class);
    private TetrisAction[] available = new TetrisAction[]{TetrisAction.MOVE_BLOCK_LEFT, TetrisAction.MOVE_BLOCK_RIGHT, TetrisAction.ROTATE_BLOCK_CW, TetrisAction.IMMEDIATE_BLOCK_PLACE};
    private Thread thinking;
    private int fitness;
    private int layer = 10;
    private double[][] w1;
    private double[][] w2;
    private double[][] w3;
    private double[][] w4;

    public Computer(int userId, PlayerKeycode playerKeycode, TetrisEventRepository repository) {
        super(userId, playerKeycode, new ComputerEventRepository(repository));

        w1 = Matrix.randn(10, layer);
        w2 = Matrix.randn(layer, 20);
        w3 = Matrix.randn(20, layer);
        w4 = Matrix.randn(layer, 4);
    }

    @Override
    public void connectToServer(TetrisServer server) {
        super.connectToServer(server);
        ((ComputerEventRepository) eventRepository).subscribe(this::choose);
    }

    private void choose(ComputerInput input) {
        int choose = input.inputs(w1, w2, w3, w4);

        switch (available[choose]) {
            case IMMEDIATE_BLOCK_PLACE -> actionRepository.immediateBlockPlace();
            case MOVE_BLOCK_LEFT -> actionRepository.moveBlockLeft();
            case MOVE_BLOCK_RIGHT -> actionRepository.moveBlockRight();
            case ROTATE_BLOCK_CW -> actionRepository.rotateBlockCW();
        }
    }
}
