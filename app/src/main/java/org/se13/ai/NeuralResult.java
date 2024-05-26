package org.se13.ai;

import java.util.Objects;
import java.util.Random;

public record NeuralResult(int fitness, Predict predict) {
    public NeuralResult(Predict predict) {
        this(-1, predict);
    }

    public NeuralResult cross(NeuralResult n2) {
        if (fitness == 0 && n2.fitness == 0) {
            Random random = new Random();
            return new NeuralResult(
                new Predict(
                    random.nextBoolean() ? predict.getHeightWeight() : n2.predict().getHeightWeight(),
                    random.nextBoolean() ? predict.getLineWeight() : n2.predict().getLineWeight(),
                    random.nextBoolean() ? predict.getHoleWeight() : n2.predict().getHoleWeight(),
                    random.nextBoolean() ? predict.getBumpinessWeight() : n2.predict().getBumpinessWeight()
                )
            );
        }

        return new NeuralResult(
            PredictUtils.normalize(new Predict(
                fitness * predict().getHeightWeight() + n2.fitness * n2.predict().getHeightWeight(),
                fitness * predict().getLineWeight() + n2.fitness * n2.predict().getLineWeight(),
                fitness * predict().getHoleWeight() + n2.fitness * n2.predict().getHoleWeight(),
                fitness * predict().getBumpinessWeight() + n2.fitness * n2.predict().getBumpinessWeight()
            ))
        );
    }

    public NeuralResult mutate(Random random) {
        float quantity = random.nextFloat() * 0.4f - 0.2f;
        float heightWeight = predict().getHeightWeight();
        float lineWeight = predict().getLineWeight();
        float holdWeight = predict().getHoleWeight();
        float bumpinessWeight = predict().getBumpinessWeight();

        switch (random.nextInt(4)) {
            case 0 -> heightWeight += quantity;
            case 1 -> lineWeight += quantity;
            case 2 -> holdWeight += quantity;
            case 3 -> bumpinessWeight += quantity;
        }

        return new NeuralResult(new Predict(heightWeight, lineWeight, holdWeight, bumpinessWeight));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeuralResult that = (NeuralResult) o;
        return Objects.equals(predict, that.predict);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(predict);
    }
}
