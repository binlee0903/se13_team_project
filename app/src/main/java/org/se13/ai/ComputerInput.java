package org.se13.ai;

import org.se13.game.block.CellID;
import org.se13.game.block.CurrentBlock;
import org.se13.utils.Matrix;

import java.util.Arrays;
import java.util.List;

public class ComputerInput {
    public CellID[][] tetrisGrid;
    public CurrentBlock nextBlock;

    public int inputs(float[][] w1, float[][] w2, float[][] w3, float[][] w4) {
        float[][] input = new float[11][22];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 22; j++) {
                input[i][j] = tetrisGrid[j][i].id;
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
            input[10][i] = next[i];
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
