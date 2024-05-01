package org.se13.view.tetris;

import org.se13.utils.Observer;
import org.se13.utils.Subscriber;

public class TetrisStateRepositoryImpl implements TetrisStateRepository {

    private final Observer<TetrisState> observer;

    private final Observer<TetrisGameEndData> isGameOver;

    public TetrisStateRepositoryImpl() {
        this.observer = new Observer<>();
        this.isGameOver = new Observer<>();
    }

    @Override
    public void response(TetrisState state) {
        this.observer.setValue(state);
    }

    @Override
    public void subscribe(Subscriber<TetrisState> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        this.observer.subscribe(subscriber);
        this.isGameOver.subscribe(isGameOver);
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        isGameOver.setValue(endData);
    }
}
