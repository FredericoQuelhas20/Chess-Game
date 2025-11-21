package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.model.data.pieces.Rook;

import java.io.*;
import java.util.ArrayList;

public class Board implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public enum type {KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN}
    private final int SIZE = 8;
    Piece[][] board = new Piece[SIZE][SIZE];


    public Board(){
        //Inicializa o Board e poe as posições vazias a null
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                board[i][j] = null;

        //Este loop distriubui os peões no Board
        for (int i = 0; i < SIZE; i++) {
            board[1][i] = Piece.createPiece('p', false, 1, i, this);
            board[6][i] = Piece.createPiece('p',true, 6, i, this);
        }

        //Distribui as restantes peças Pretas no Board È fixe
        board[0][0] = Piece.createPiece('r',false, 0, 0, this);
        board[0][1] = Piece.createPiece('n',false, 0, 1, this);
        board[0][2] = Piece.createPiece('b',false, 0, 2, this);
        board[0][3] = Piece.createPiece('q',false, 0, 3, this);
        board[0][4] = Piece.createPiece('k',false, 0, 4, this);
        board[0][5] = Piece.createPiece('b',false, 0, 5, this);
        board[0][6] = Piece.createPiece('n',false, 0, 6, this);
        board[0][7] = Piece.createPiece('r',false, 0, 7, this);

        //Distribui as restantes peças pretas no Board
        board[7][0] = Piece.createPiece('R',true, 7, 0, this);
        board[7][1] = Piece.createPiece('N',true, 7, 1, this);
        board[7][2] = Piece.createPiece('B',true, 7, 2, this);
        board[7][3] = Piece.createPiece('Q',true, 7, 3, this);
        board[7][4] = Piece.createPiece('K',true, 7, 4, this);
        board[7][5] = Piece.createPiece('B',true, 7, 5, this);
        board[7][6] = Piece.createPiece('N',true, 7, 6, this);
        board[7][7] = Piece.createPiece('R',true, 7, 7, this);

    }
    public Board(Piece[][]board){
        //this.board=board;// Construtor de uma board ja existente
        for (int i = 0; i < 8; i++){
            System.arraycopy(board[i], 0, this.board[i], 0, 8);
        }
    }


    public Piece getPiece(int line, int column){ //Retorna a peça na posição line, column
        if(line < 0 || line >= SIZE || column < 0 || column >= SIZE)
            return null;
        return board[line][column];
    }
    public Piece getKing(boolean currentPlayer){ //Retorna a peça na posição line, column
        for (Piece[] pArr:board){
            for (Piece p: pArr){
                if (p != null && p.getType() == type.KING){
                    if (p.getColor() == currentPlayer){
                        return p;
                    }
                }
            }
        }
        return null;
    }
    public int getSize(){
        return SIZE;
    }
    public boolean addPiece(Piece p){ //Adiciona uma peça ao Board
        if(p == null)
            return false;

        if(p.getLine() < 0 || p.getLine() >= SIZE || p.getColumn() < 0 || p.getColumn() >= SIZE)
            return false;

        if(board[p.getLine()][p.getColumn()] != null)
            return false;

        board[p.getLine()][p.getColumn()] = p;
        return true;
    }

    //Remove a peça na posição line, column
    public void removePiece(int line, int column){
        if(line < 0 || line >= SIZE || column < 0 || column >= SIZE)
            return;

        if(board[line][column] == null)
            return;

        board[line][column] = null;
    }
    public void putPiece(Piece p, int line, int column){
        if(line < 0 || line >= SIZE || column < 0 || column >= SIZE)
            return;
        board[line][column] = p;
    }


    public void movePiecesForCastle(King king, Rook rook){
        int newColumnKing = king.getColumn();
        int newColumnRook = rook.getColumn();
        newColumnKing += rook.getColumn() == 0?-2:2;
        newColumnRook += rook.getColumn() == 0?3:-2;
        System.out.println(newColumnKing + " " + newColumnRook);

        removePiece(king.getLine(), king.getColumn());
        board[king.getLine()][newColumnKing] = king;
        king.setPosicao(king.getLine(), newColumnKing, false);

        removePiece(rook.getLine(), rook.getColumn());
        board[rook.getLine()][newColumnRook] = rook;
        rook.setPosicao(rook.getLine(), newColumnRook, false);

    }

    //Método para mover a peça na posição linha, coluna para a posição novaLinha, novaColuna
    //Retorna true se a peça foi movida com sucesso, false caso contrário

    public boolean kingStillUnderThreat(boolean currentPlayer, int linha, int coluna, int novaLinha, int novaColuna){
        Piece p = board[linha][coluna];
        boolean kingUnderAttack;
        if(p == null)
            return false;
        if(!p.canMove(novaLinha, novaColuna)){ //Verifica se a peça pode se mover para aquela posi tendo em conta as regras
            return false;
        }
        Piece k = getKing(currentPlayer);
        Board stateBeforeMove = new Board(board); // Copia o estado da board (nao encontrei outra maneira)
        removePiece(linha, coluna);
        board[novaLinha][novaColuna] = p;
        p.setPosicao(novaLinha, novaColuna, true);
        kingUnderAttack = isUnderThreat(currentPlayer,k.getLine(),k.getColumn()); // verifica se ainda está a se atacado
        board = stateBeforeMove.board;
        p.setPosicao(linha, coluna, true);
        return kingUnderAttack;
    }
    public boolean isUnderThreat(boolean currentPlayer, int line, int column){
        for (int i = 0; i < 8; i++){
            for(int j = 0; j<8; j++){
                Piece p = getPiece(i, j);
                if(p != null && p.getColor() == !currentPlayer) {
                    if (p.canMove(line, column)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public boolean posicaoValida(int novaLinha, int novaColuna) { //Verifica se a posição é válida (ou seja dentro do Board)
        return novaLinha >= 0 && novaLinha < SIZE && novaColuna >= 0 && novaColuna < SIZE;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece p = board[i][j];
                if(p == null)
                    sb.append("  ");
                else
                    sb.append(p.getPosicao()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String printBoard(){ //Imprime o Board para testes
        StringBuilder sb = new StringBuilder();
        sb.append("  a b c d e f g h\n");
        for (int i = 0; i < SIZE; i++){
            sb.append(8 - i).append(" ");
            for(int j = 0; j < SIZE; j++)
                sb.append(board[i][j] == null ? "." : board[i][j].getSimbolo()).append(" ");
            sb.append(8 - i).append("\n");
        }
        sb.append("  a b c d e f g h\n");
        return sb.toString();
    }

    public void printMovesFromPiece(int line, int column){
        Piece p = getPiece(line, column);
        ArrayList<int[]> moves = p.getPossibleMoves();
        if (moves == null){
            System.out.println("nao");
        }

        assert moves != null;
        for (int[] move : moves) {
            char col = (char) (move[1] + 'a');
            int row = 8 - move[0];
            System.out.print(col + "" + row + " ");
        }
        System.out.println();
    }

    //Verifica se o rei está em check
    public boolean isCheck(boolean currentPlayer) {
        Piece p = getKing(currentPlayer);
        return p!= null && isUnderThreat(currentPlayer, p.getLine(), p.getColumn());
    }

    public ArrayList<int[]> getMovesOfPiece(int line, int column){
        return board[line][column].getPossibleMoves();
    }

    public Piece movePieceForUndo(int linha, int coluna, int novaLinha, int novaColuna) {
        if(!posicaoValida(novaLinha, novaColuna) || !posicaoValida(linha, coluna))
            return null;

        Piece p = board[linha][coluna];
        if(p == null)
            return null;

        // Move a peça sem nenhuma validação
        board[linha][coluna] = null;
        board[novaLinha][novaColuna] = p;
        p.setPosicao(novaLinha, novaColuna, false);
        return p;
    }


    /*public Board deepCopy() {
       *//* try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(this);
            try (
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    ObjectInputStream ois = new ObjectInputStream(bais)
            ) {
                return (Board) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to deep copy Board", e);
        }*//*
        Board copy = new Board();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Piece original = this.board[i][j];
                if (original != null) {
                    copy.board[i][j] = Piece.createPiece(
                            original.getSimbolo().charAt(0),
                            original.getColor(),
                            i,
                            j,
                            copy  // Referência ao novo Board
                    );
                }
            }
        }
        return copy;

    }*/

    public Board deepCopy() {
        Board copy = new Board();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] != null) {
                    Piece original = this.board[i][j];
                    Piece pieceCopy = Piece.createPiece(
                            original.getSimbolo().charAt(0),
                            original.getColor(),
                            i,
                            j,
                            copy  // Passa a nova referência do tabuleiro
                    );
                    copy.board[i][j] = pieceCopy;
                }
            }
        }
        return copy;
    }
    public Piece[][] getBoard(){
        return deepCopy().board;
    }
}