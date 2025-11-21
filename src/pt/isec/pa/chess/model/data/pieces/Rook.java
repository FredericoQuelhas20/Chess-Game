package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;

import java.io.Serial;
import java.util.ArrayList;

public class Rook extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean moved = false;

    public Rook(Boolean white, int line, int column, Board b){
        super(white, line, column, b);
    }
    @Override
    public void setPosicao(int novaLinha, int novaColuna, boolean simulation) {
        super.setPosicao(novaLinha, novaColuna, simulation);
        if(!simulation)
            moved = true;
    }

    public boolean getMoved(){
        return moved;
    }

    public void setMoved(boolean hasMoved) {
        this.moved = hasMoved;
    }

    @Override
    public String getSimbolo() {
        return getColor() ? "R" : "r";
    }

    @Override
    public String getPosicao() {
        char col = (char) (getColumn() + 'a');
        int linha = 8 - getLine();
        return getSimbolo() + col + linha + (moved ? "" : "*");
    }

    @Override
    public Board.type getType() {
        return Board.type.ROOK;
    }

    @Override
    public boolean canMove(int novaLinha, int novaColuna) {
        if (!validPosition(novaLinha, novaColuna))
            return false;

        //Estas duas linhas de código determinam:
        int dl = Math.abs(novaLinha - getLine()); //A diferença entre a nova linha e a linha atual
        int dc = Math.abs(novaColuna - getColumn()); //A diferença entre a nova coluna e a coluna atual

        if(dl != 0 && dc != 0) //Vai dar diferente de 0 se o movimento não for reto
            return false; // Retorna falso se o trajeto não for reto

        int passoLinha = Integer.compare(novaLinha, getLine()); //Determina se a peça vai subir ou descer
        int passoColuna = Integer.compare(novaColuna, getColumn()); //Determina se a peça vai para a esquerda ou direita
        int linhaAtual = getLine() + passoLinha; //Determina a linha atual
        int colunaAtual = getColumn() + passoColuna; //Determina a coluna atual

        //Este loop faz a verificação se existe alguma peça no caminho
        while(linhaAtual != novaLinha || colunaAtual != novaColuna){
            if(getPiece(linhaAtual, colunaAtual) != null)
                return false;
            linhaAtual += passoLinha;
            colunaAtual += passoColuna;
        }

        Piece destino = getPiece(novaLinha, novaColuna);
        return destino == null || destino.getColor() != getColor();
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

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
