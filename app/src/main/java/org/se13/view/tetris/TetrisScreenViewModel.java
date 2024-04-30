package org.se13.view.tetris;

import org.se13.utils.Subscriber;

public class TetrisScreenViewModel implements TetrisActionRepository {
    private TetrisActionRepository actionRepository;
    private TetrisStateRepository stateRepository;

    public TetrisScreenViewModel(TetrisActionRepository actionRepository, TetrisStateRepository stateRepository) {
        this.actionRepository = actionRepository;
        this.stateRepository = stateRepository;
    }

    public void observe(Subscriber<TetrisState> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        stateRepository.subscribe(subscriber, isGameOver);
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
