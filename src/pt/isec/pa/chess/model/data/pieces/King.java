package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Player;

import java.io.Serial;
import java.util.ArrayList;

public class King extends Piece {
    @Serial
    private static final long serialVersionUID = 1L;

    private boolean moved = false;

    public King(boolean white, int line, int column, Board b) {
        super(white, line, column, b);
    }

    @Override
    public void setPosicao(int novaLinha, int novaColuna, boolean simulation) {
        super.setPosicao(novaLinha, novaColuna, simulation);
        if(!simulation){
            moved = true;
        }
    }

    @Override
    public String getPosicao() {
        char col = (char) (getColumn() + 'a');
        int linha = 8 - getLine();
        return getSimbolo() + col + linha + (moved ? "" : "*");
    }

    @Override
    public Board.type getType() {
        return Board.type.KING;
    }


    @Override
    public String getSimbolo() {
        return getColor()?"K":"k";
    }


    @Override
    public ArrayList<int[]> getPossibleMoves() {
        ArrayList<int[]> moves = new ArrayList<>();
        int[][] kingMoves = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] move : kingMoves) {
            int newLine = getLine() + move[0];
            int newCol = getColumn() + move[1];

            if (canMove(newLine, newCol) && !getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), newLine, newCol)) {
                moves.add(new int[]{newLine, newCol});
            }
        }

        return moves;
    }
    public boolean attackerCanBeEaten(Player attacker, Player victim){
        var pieces = victim.getPieces();
        Piece attackerPiece = getPieceFromMove(attacker, getLine(), getColumn());
        if (attackerPiece == null){
            return false;
        }
        Piece defenderPiece = getPieceFromMove(victim, attackerPiece.getLine(), attackerPiece.getColumn());
        if(defenderPiece != null){
            if(defenderPiece.getType() == Board.type.KING){
                System.out.println("Log attackerCanBeEatemn1: " + defenderPiece);
                return getBoard().kingStillUnderThreat(defenderPiece.getColor(), defenderPiece.getLine(), defenderPiece.getColumn(),
                        attackerPiece.getLine(),attackerPiece.getColumn());
            }
            else{
                System.out.println("Log attackerCanBeEatemn2: " + defenderPiece);
                return true;
            }
        }
        return false;
    }
    public boolean canBeDefended(Player attacker, Player victim){
        King victimKing = (King)getBoard().getKing(victim.getColor());
        Piece kingAttacker = getPieceFromMove(attacker, victimKing.getLine(), victimKing.getColumn());
        if (kingAttacker == null){
            System.out.println("É null");
            return true;
        }
        var moves = kingAttacker.getPossibleMoves();
        for(var move : moves){
            var defender = getPieceFromMove(victim, move[0], move[1]);
            if(defender != null){
                //System.out.println("no canbedefended: " + getBoard().kingStillUnderThreat(victim.getColor(), defender.getLine(), defender.getColumn(), move[0], move[1]) + " " + canDodge());
                if(defender.canMove(move[0], move[1])){
                    System.out.println(defender + ", " + move[0] + " " + move[1]);
                    if(!getBoard().kingStillUnderThreat(victim.getColor(), defender.getLine(), defender.getColumn(), move[0], move[1])){
                        return true;
                    }
                }
           }
        }
        return false;
    }
    public boolean canDodge(){
        var moves = getPossibleMoves();
        for(var move : moves){
            System.out.println(!getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), move[0], move[1]));
            if(!getBoard().kingStillUnderThreat(getColor(), getLine(), getColumn(), move[0], move[1])){
                return true;
            }
        }
        return false;
    }
    public Piece getPieceFromMove(Player player, int line, int column){
        var pieces = player.getPieces();
        for(var piece : pieces){
            if(piece != null){
                var moves = piece.getPossibleMoves();
                for(var move : moves){
                    if(line == move[0] && column == move[1]){
                        return piece;
                    }
                }
            }
        }
        return null;
    }
    @Override
    public boolean canMove(int  novaLinha, int novaColuna) {
        if(!validPosition(novaLinha, novaColuna)) //se a posicao nao for valida (ou seja fora do tabuleiro)
            return false;

        //Estas duas linhas de código determinam:
        int dl = Math.abs(novaLinha - getLine());//a diferença entre a nova linha e a linha atual
        int dc = Math.abs(novaColuna - getColumn()); //a diferença entre a nova coluna e a coluna atual

        if(dl > 1 || dc > 1) //Garante que o rei só se move uma casa
            return false;

        Piece destino = getPiece(novaLinha, novaColuna);
        if(destino != null && destino.getColor() == getColor())
            return false; // Ainda é necessario verificar se está a ser defendida

        Board tempBoard = new Board();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                Piece original = getBoard().getPiece(i, j);
                if (original != null) {
                    tempBoard.addPiece(Piece.createPiece(original.getSimbolo().charAt(0),
                            original.getColor(), i, j, tempBoard));
                }
            }
        }

        // Simula o movimento
        tempBoard.removePiece(getLine(), getColumn());
        Piece tempKing = new King(getColor(), novaLinha, novaColuna, tempBoard);
        tempBoard.addPiece(tempKing);

        // Verifica se a nova posição está sob ameaça
        for (int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                Piece p = tempBoard.getPiece(i, j);
                if(p != null && p.getColor() != getColor()) {
                    if (p.canMove(novaLinha, novaColuna)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void setMoved(boolean hasMoved) {
        this.moved = hasMoved;
    }

    public boolean getMoved(){
        return moved;
    }
}
