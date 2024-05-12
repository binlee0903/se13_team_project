package org.se13.view.tetris;

import org.se13.game.event.TetrisEvent;
import org.se13.utils.Subscriber;

public class TetrisScreenViewModel implements TetrisActionRepository {
    private TetrisActionRepository actionRepository;
    private TetrisEventRepository eventRepository;

    public TetrisScreenViewModel(TetrisActionRepository actionRepository, TetrisEventRepository stateRepository) {
        this.actionRepository = actionRepository;
        this.eventRepository = stateRepository;
    }

    public void observe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        eventRepository.subscribe(subscriber, isGameOver);
    }

    @Override
    public void connect() {
        actionRepository.connect();
    }

    @Override
    public void immediateBlockPlace() {
        actionRepository.immediateBlockPlace();
    }

    @Override
    public void moveBlockDown() {
        actionRepository.moveBlockDown();
    }

    @Override
    public void moveBlockLeft() {
        actionRepository.moveBlockLeft();
    }

    @Override
    public void moveBlockRight() {
        actionRepository.moveBlockRight();
    }

    @Override
    public void rotateBlockCW() {
        actionRepository.rotateBlockCW();
    }

    @Override
    public void togglePauseState() {
        actionRepository.togglePauseState();
    }

    @Override
    public void exitGame() {
        actionRepository.exitGame();
    }
}
