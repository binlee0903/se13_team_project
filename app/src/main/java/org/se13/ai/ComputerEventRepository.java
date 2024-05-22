package org.se13.ai;

import org.se13.game.event.AttackedTetrisBlocks;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.UpdateTetrisState;
import org.se13.utils.Observer;
import org.se13.utils.Subscriber;
import org.se13.view.tetris.TetrisEventRepository;
import org.se13.view.tetris.TetrisGameEndData;

public class ComputerEventRepository implements TetrisEventRepository {
    private TetrisEventRepository delegate;
    private Observer<TetrisEvent> forComputer = new Observer<>();
    private Observer<TetrisGameEndData> forEnd = new Observer<>();
    private Observer<ComputerInput> observer = new Observer<>();

    public ComputerEventRepository(TetrisEventRepository repository) {
        this.delegate = repository;
        observer.setValue(new ComputerInput());
    }

    @Override
    public void gameOver(TetrisGameEndData endData) {
        delegate.gameOver(endData);
        forEnd.setValue(endData);
    }

    @Override
    public void response(TetrisEvent event) {
        delegate.response(event);
        forComputer.setValue(event);

        ComputerInput current = observer.getValue();
        switch (event) {
            case UpdateTetrisState state -> {
                current.nextBlock = state.nextBlock();
                current.tetrisGrid = state.tetrisGrid();
            }
            default -> {
            }
        }
        observer.setValue(current);
    }

    @Override
    public void subscribe(Subscriber<TetrisEvent> subscriber, Subscriber<TetrisGameEndData> isGameOver) {
        delegate.subscribe(subscriber, isGameOver);
    }

    public void subscribe(Subscriber<ComputerInput> subscriber) {
        observer.subscribe(subscriber);
    }

    public void subscribeEvent(Subscriber<TetrisEvent> subscriber) {
        forComputer.subscribe(subscriber);
    }

    public void subscribeEnd(Subscriber<TetrisGameEndData> subscriber) {
        forEnd.subscribe(subscriber);
    }
}
