package pt.isec.pa.chess.model.memento;

import java.util.ArrayDeque;
import java.util.Deque;

public class CareTaker {
    private final IOriginator originator;
    private final Deque<IMemento> history = new ArrayDeque<>();
    private final Deque<IMemento> redoHistory = new ArrayDeque<>();

    public CareTaker(IOriginator originator) {
        this.originator = originator;
    }

    public void save(IMemento memento){
        redoHistory.clear();
        history.push(memento);
    }

    public void undo(){
        if(history.isEmpty()){
            return;
        }
        IMemento last = history.pop();
        redoHistory.push(originator.save());
        originator.restore(last);
    }

    public void redo(){
        if(redoHistory.isEmpty()){
            return;
        }

        history.push(originator.save());
        IMemento next = redoHistory.pop();
        originator.restore(next);
    }

    public void reset(){
        history.clear();
        redoHistory.clear();
    }

    public boolean hasUndo() {
        return !history.isEmpty();
    }

    public boolean hasRedo() {
        return !redoHistory.isEmpty();
    }
}
