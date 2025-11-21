package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;

import java.io.Serial;
import java.util.ArrayList;

public class Queen extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    public Queen(Boolean white, int line, int column, Board b){ super(white, line, column, b); }

    @Override
    public String getSimbolo() {
        return getColor() ? "Q" : "q";
    }

    @Override
    public String getPosicao() {
        char col = (char) (getColumn() + 'a');
        int linha = 8 - getLine();
        return getSimbolo() + col + linha;
    }

    @Override
    public Board.type getType() {
        return Board.type.QUEEN;
    }

    @Override
    public boolean canMove(int novaLinha, int novaColuna) {
        if(!validPosition(novaLinha, novaColuna)) //Se a posição não for válida (ou seja se for fora do tabuleiro)
            return false;

        //Estas variaveis determinam:
        int dl = Math.abs(novaLinha - getLine()); //A diferença entre a linha atual e a linha para onde a peça se quer mover
        int dc = Math.abs(novaColuna - getColumn()); //A diferença entre a coluna atual e a coluna para onde a peça se quer mover

        if(!(dl == 0 || dc == 0 || dl == dc)){ //Verifica se a peça move se em linha reta ou em diagonal
            // mais explicação no Bishop.java e Rook.java
            return false;
        }

        int passoLinha = Integer.compare(novaLinha, getLine()); //Determina se a peça se move para cima ou para baixo
        int passoColuna = Integer.compare(novaColuna, getColumn()); //Determina se a peça se move para a esquerda ou para a direita
        int linhaAtual = getLine() + passoLinha; //A linha atual da peça
        int colunaAtual = getColumn() + passoColuna; //A coluna atual da peça

        //Este loop verifica se a peça não tem nenhuma peça no caminho
        while(linhaAtual != novaLinha || colunaAtual != novaColuna){
            if(getPiece(linhaAtual, colunaAtual) != null){
                return false;
            }
            linhaAtual += passoLinha;
            colunaAtual += passoColuna;
        }

        Piece destino = getPiece(novaLinha, novaColuna);
        return destino == null || destino.getColor() != getColor();
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            int newLine = getLine() + dir[0];
            int newCol = getColumn() + dir[1];

            while (validPosition(newLine, newCol)) {
                if (canMove(newLine, newCol) && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), newLine, newCol)) {
                    moves.add(new int[]{newLine, newCol});
                } else {
                    break;
                }
                newLine += dir[0];
                newCol += dir[1];
            }
        }
        return moves;
    }
}
