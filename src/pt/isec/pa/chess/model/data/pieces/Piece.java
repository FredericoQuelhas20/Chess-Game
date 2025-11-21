package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Piece implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final boolean white;
    private int line;
    private int column;
    private Board board;

    public boolean getColor(){
        return white;
    }
    public int getLine(){
        return line;
    }

    public Board getBoard() {
        return board;
    }

    public int getColumn(){
        return column;
    }
    public Piece(boolean white, int line, int column, Board b) {
        this.white = white;
        this.line = line;
        this.column = column;
        this.board = b;
    }

    public boolean validPosition(int line, int column){return board.posicaoValida(line, column);}
    public Piece getPiece(int line, int column){return board.getPiece(line, column);}

    public void setPosicao(int novaLinha, int novaColuna, boolean simulation) {
        this.line = novaLinha;
        this.column = novaColuna;
    }

    public int getSize(){
        return board.getSize();
    }

    public abstract String getPosicao();
    public abstract Board.type getType();
    public abstract String getSimbolo();
    public abstract boolean canMove(int novaLinha, int novaColuna);
    public abstract ArrayList<int[]>  getPossibleMoves();

    public static Piece createPiece(Board.type type, boolean white, int line, int column, Board board) {
        return switch (type) {
            case PAWN -> new Pawn(white, line, column, board);
            case ROOK -> new Rook(white, line, column, board);
            case KNIGHT -> new Knight(white, line, column, board);
            case BISHOP -> new Bishop(white, line, column, board);
            case QUEEN -> new Queen(white, line, column, board);
            case KING -> new King(white, line, column, board);
        };
    }
    public static Piece createPiece(Character type, boolean white, int line, int column, Board board) {
            if(type == 'P' || type == 'p')
                return new Pawn(white, line, column, board);
            if(type == 'R' || type == 'r')
                return new Rook(white, line, column, board);
            if(type == 'N' || type == 'n')
                return new Knight(white, line, column, board);
            if(type == 'B' || type == 'b')
                return new Bishop(white, line, column, board);
            if(type == 'Q' || type == 'q')
                return new Queen(white, line, column, board);
            if(type == 'K' || type == 'k')
                return new King(white, line, column, board);
            return null;
    }
    public static Piece createPiece(String type, Board board){
        if (type == null || type.length() < 2) return null;

        char tipo = type.charAt(0);
        boolean isBranco = Character.isUpperCase(tipo);
        tipo = Character.toLowerCase(tipo);

        String posicao = type.substring(1);
        int coluna = posicao.charAt(0) - 'a';
        int linha = 8 - Character.getNumericValue(posicao.charAt(1));

        switch (tipo) {
            case 'k' -> { return new King(isBranco, linha, coluna, board); }
            case 'q' -> { return new Queen(isBranco, linha, coluna, board); }
            case 'r' -> { return new Rook(isBranco, linha, coluna, board); }
            case 'b' -> { return new Bishop(isBranco, linha, coluna, board); }
            case 'n' -> { return new Knight(isBranco, linha, coluna, board); }
            case 'p' -> { return new Pawn(isBranco, linha, coluna, board); }
            default -> { return null; }
        }
    }


    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Piece p))
            return false;
        return p.getLine()==line && p.getColumn()==column;
    }


    public void setBoard(Board b) {
        this.board = b;
    }
}
