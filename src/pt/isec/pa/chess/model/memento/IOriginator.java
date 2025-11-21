package pt.isec.pa.chess.model.memento;

public interface IOriginator {
    IMemento save();
    void restore(IMemento memento);
}
