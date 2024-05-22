package org.se13.ai;

import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.game.grid.TetrisGrid;
import org.se13.utils.Matrix;

import java.util.Arrays;
import java.util.List;

public class ComputerInput {
    public CellID[][] tetrisGrid;
    public CurrentBlock nextBlock;
    public CellID[][] attacked;

    public ComputerInput() {
        attacked = new TetrisGrid(22, 10).getGrid();
    }

    public int inputs(float[][] w1, float[][] w2, float[][] w3, float[][] w4) {
        float[][] input = new float[46][10];

        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 10; j++) {
                input[i][j] = tetrisGrid[i][j].id;
            }
        }

        for (int i = 0; i < attacked.length; i++) {
            for (int j = 0; j < attacked[i].length; j++) {
                input[i + 22][j] = attacked[i][j].id;
            }
        }

        List<float[]> convert = Arrays.stream(nextBlock.cells()).map((cell) -> {
            int r = cell.position().getRowIndex();
            int c = cell.position().getColIndex();
            int id = cell.cellID().id;

            return new float[]{r, c, id};
        }).toList();

        float[] next = new float[convert.size() * 3];

        for (int i = 0; i < convert.size(); i++) {
            float[] in = convert.get(i);
            for (int j = 0; j < 3; j++) {
                next[3 * i + j] = in[j];
            }
        }

        for (int i = 0; i < next.length; i++) {
            if (i < 9) {
                input[44][i] = next[i];
            } else {
                input[45][i - 9] = next[i - 9];
            }
        }

        float[][] net;

        net = Matrix.matmul(input, w1);
        net = Matrix.relu(net);
        net = Matrix.matmul(net, w2);
        net = Matrix.relu(net);
        net = Matrix.matmul(net, w3);
        net = Matrix.relu(net);
        net = Matrix.matmul(net, w4);
        net = Matrix.relu(net);

        return Matrix.argmax(net[0]);
    }
}
