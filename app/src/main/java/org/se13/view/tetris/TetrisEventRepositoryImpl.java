package org.se13.view.tetris;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Observer;
import org.se13.utils.Subscriber;

public class TetrisEventRepositoryImpl implements TetrisEventRepository {

    private final Observer<TetrisEvent> observer;

    private final Observer<TetrisGameEndData> isGameOver;

    public TetrisEventRepositoryImpl() {
        this.observer = new Observer<>();
        this.isGameOver = new Observer<>();
    }

    @Override
    public void response(TetrisEvent event) {
        this.observer.setValue(event);
    }

    @Override
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        this.observer.subscribe(subscriber);
        this.isGameOver.subscribe(isGameOver);
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        isGameOver.setValue(endData);
    }
}
