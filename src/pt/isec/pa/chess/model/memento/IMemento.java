package pt.isec.pa.chess.model.memento;

public interface IMemento {
    default Object getSnapshot(){ return null; }
}
