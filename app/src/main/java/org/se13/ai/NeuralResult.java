package org.se13.ai;

import java.util.Objects;
import java.util.Random;

public record NeuralResult(int fitness, Predict predict) {
    public NeuralResult(Predict predict) {
        this(-1, predict);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NeuralResult that = (NeuralResult) o;
        return fitness == that.fitness && Objects.equals(predict, that.predict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fitness, predict);
    }

    public NeuralResult cross(NeuralResult n2) {
        return new NeuralResult(
                new Predict(
                        fitness * predict().getHeightWeight() + n2.fitness * n2.predict().getHeightWeight(),
                        fitness * predict().getLineWeight() + n2.fitness * n2.predict().getLineWeight(),
                        fitness * predict().getHoleWeight() + n2.fitness * n2.predict().getHoleWeight(),
                        fitness * predict().getBumpinessWeight() + n2.fitness * n2.predict().getBumpinessWeight()
                )
        );
    }

    public NeuralResult mutate(Random random) {
        double quantity = random.nextDouble() * 0.4 - 0.2;
        double heightWeight = predict().getHeightWeight();
        double lineWeight = predict().getLineWeight();
        double holdWeight = predict().getHoleWeight();
        double bumpinessWeight = predict().getBumpinessWeight();

        switch (random.nextInt(4)) {
            case 0 -> heightWeight += quantity;
            case 1 -> lineWeight += quantity;
            case 2 -> holdWeight += quantity;
            case 3 -> bumpinessWeight += quantity;
        }

        return new NeuralResult(new Predict(heightWeight, lineWeight, holdWeight, bumpinessWeight));
    }
}
