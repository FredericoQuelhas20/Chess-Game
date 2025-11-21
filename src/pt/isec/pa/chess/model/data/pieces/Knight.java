package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;

import java.io.Serial;
import java.util.ArrayList;

class Knight extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    public Knight(Boolean white, int line, int column, Board b){ super(white, line, column, b); }

    @Override
    public String getSimbolo() {
        return getColor() ? "N" : "n";
    }

    @Override
    public Board.type getType(){return Board.type.KNIGHT;}

    @Override
    public String getPosicao() {
        char col = (char) (getColumn() + 'a');
        int linha = 8 - getLine();
        return getSimbolo() + col + linha;
    }

    @Override
    public boolean canMove(int novaLinha, int novaColuna) {
        if(!validPosition(novaLinha, novaColuna)) //Se a posição não for válida (ou seja se for fora do tabuleiro)
            return false;

        int dl = Math.abs(novaLinha - getLine()); //A diferença entre a linha atual e a linha para onde a peça se quer mover
        int dc = Math.abs(novaColuna - getColumn()); //A diferença entre a coluna atual e a coluna para onde a peça se quer mover

        //Verifica se a peça move se em L
        //Faz isso verificando se a diferença entre a linha e a coluna é 2 e 1
        if(!((dl == 2 && dc == 1) || (dl == 1 && dc == 2))){
            return false;
        }

        Piece destino = getPiece(novaLinha, novaColuna);
        return destino == null || destino.getColor() != getColor();
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        int xAxis, yAxis;
        for (int i = -2; i <= 2; i+=4){
            xAxis = getColumn()+i;
            yAxis = getLine()+i;
            for (int j = -1; j <= 1; j+=2){
                if(canMove(getLine()+j,xAxis) && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), getLine()+j, xAxis)){
                    moves.add(new int[]{getLine()+j, xAxis});
                }
                if(canMove(yAxis,getColumn()+j) && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), yAxis,getColumn()+j)){
                    moves.add(new int[]{yAxis, getColumn()+j});
                }
            }
        }
        return moves;
    }

}
