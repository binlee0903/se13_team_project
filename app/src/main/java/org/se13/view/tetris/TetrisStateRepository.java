package org.se13.view.tetris;

import org.se13.utils.Subscriber;

public interface TetrisStateRepository {

    public void gameOver(TetrisGameEndData endData);

    public void response(TetrisState state);

    public void subscribe(Subscriber<TetrisState> subscriber, Subscriber<TetrisGameEndData> isGameOver);
}
