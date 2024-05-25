package org.se13.ai;

import java.util.ArrayList;
import java.util.List;

public record SaveData(List<NeuralResult> neuralList) {

    @Override
    public List<NeuralResult> neuralList() {
        if (neuralList == null) return new ArrayList<>();
        return neuralList;
    }

    public NeuralResult get(int index) {
        return neuralList.get(index);
    }
}
