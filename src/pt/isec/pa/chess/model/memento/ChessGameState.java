package pt.isec.pa.chess.model.memento;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Player;

import java.io.Serial;
import java.io.Serializable;

public class ChessGameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public final Board board;
    public final boolean currentPlayer;
    public final String whitePlayerNome;
    public final String blackPlayerNome;
    public final boolean draw;
    public Player white;
    public Player black;

    public ChessGameState(Board board, boolean currentPlayer,
                          String white, String black, boolean draw ) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.whitePlayerNome = white;
        this.blackPlayerNome = black;
        this.draw = draw;
    }
}
