package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;

import java.io.Serial;
import java.util.ArrayList;

public class Pawn extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;
    private boolean enPessant;
    private boolean moved = false;

    public Pawn(boolean white, int line, int column, Board b){
        super(white, line, column, b);
        enPessant = false;
    }
    public boolean getMoved() {
        return moved;
    }
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public String getSimbolo() {
        return getColor()?"P":"p";
    }

    @Override
    public String getPosicao() {
        char col = (char) (getColumn() + 'a');
        int linha = 8 - getLine();
        return getSimbolo() + col + linha;
    }


    public boolean isEnPessant() {
        return enPessant;
    }
    public void changeEnPessant() {
        enPessant = !enPessant;
    }

    @Override
    public Board.type getType() {
        return Board.type.PAWN;
    }

    @Override
    public boolean canMove(int novaLinha, int novaColuna) {
        if(!validPosition(novaLinha, novaColuna)) return false; //Verifica se a posi é valida ou seja se está dentro do tabuleiro

        int dl = novaLinha - getLine();//Diferença entre a linha atual e a linha para onde a peça se quer mover
        int dc = Math.abs(novaColuna - getColumn());//Diferença entre a coluna atual e a coluna para onde a peça se quer mover
        int direcao = getColor() ? -1 : 1;//Direção para onde a peça se quer mover

        if(dc == 0){//Se a coluna for a mesma
            if(dl == direcao){ //Se a linha for a linha para onde a peça se quer mover
                if(getPiece(novaLinha, novaColuna) == null){
                    return true;//Se a posição para onde a peça se quer mover estiver vazia
                }
            }
            if(dl == 2*direcao && !moved){//Se a linha for a linha para onde a peça se quer mover e se a peça ainda não se moveu
                if(getPiece(novaLinha, novaColuna) == null && getPiece(novaLinha - direcao, novaColuna) == null){
                    enPessant = true;
                    return true;//Se a posição para onde a peça se quer mover e a posição intermédia estiverem vazias
                }
            }
        }

        //Movimento de captura
        if(dc == 1 && dl == direcao){//Se a coluna for adjacente e a linha for a linha para onde a peça se quer mover
            Piece destino = getPiece(novaLinha, novaColuna);
            Piece piecePessant = getPiece(getLine(), novaColuna);

            if(destino != null && destino.getColor() != getColor()){
                return true;//Se a posição para onde a peça se quer mover estiver ocupada por uma peça adversária
            }
            if(piecePessant == null){
                return false;
            }
            if(piecePessant.getType() != Board.type.PAWN){
                return false;
            }
            return destino == null && piecePessant.getColor() != getColor() && ((Pawn) piecePessant).isEnPessant(); // Se a posição para onde a peça quer se mover estiver vazia e as condições de Pessant se verifiquem
        }

        return false;
    }

    @Override
    public ArrayList<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        int direction = getColor() ? -1 : 1;
        int startLine = getColor() ? 6 : 1;

        // Movimento para frente (1 casa)
        int nextLine = getLine() + direction;
        if (validPosition(nextLine, getColumn()) && canMove(nextLine, getColumn())
                && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), nextLine, getColumn())) {
            moves.add(new int[]{nextLine, getColumn()});
        }

        // Movimento inicial de 2 casas
        int nextLine2 = getLine() + 2 * direction;
        if (getLine() == startLine && validPosition(nextLine2, getColumn()) && canMove(nextLine2, getColumn())
                && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), nextLine2, getColumn())) {
            moves.add(new int[]{nextLine2, getColumn()});
        }

        // Capturas diagonais
        for (int dc = -1; dc <= 1; dc += 2) {
            int col = getColumn() + dc;
            if (validPosition(nextLine, col) && canMove(nextLine, col)
                    && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), nextLine, col)) {
                moves.add(new int[]{nextLine, col});
            }
        }
        return moves;
    }
}
