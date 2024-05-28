package org.se13.view.tetris;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Subscriber;

public interface TetrisEventRepository {

    public void gameOver(TetrisGameEndData endData);

    public void response(TetrisEvent event);

    default void response(TetrisEvent event, int userId) {
        response(event);
    }

    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver);
}
