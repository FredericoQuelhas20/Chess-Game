package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.*;
import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Piece;
import pt.isec.pa.chess.model.data.pieces.Rook;
import pt.isec.pa.chess.model.memento.ChessGameState;
import pt.isec.pa.chess.model.memento.IMemento;
import pt.isec.pa.chess.model.memento.IOriginator;
import pt.isec.pa.chess.model.memento.Memento;

import java.io.*;
import java.util.ArrayList;

/**
 * The ChessGame class represents the core logic of a chess game, including board state,
 * piece movements, and game rules. It implements the IOriginator interface for memento
 * pattern support.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Chess board initialization and state</li>
 *   <li>Piece movement validation</li>
 *   <li>Special moves (castling, en passant, promotion)</li>
 *   <li>Game state checking (check, checkmate, stalemate)</li>
 *   <li>Game serialization and deserialization</li>
 * </ul>
 *
 * @see IOriginator
 * @see Board
 * @see Player
 */
public class ChessGame implements Serializable, IOriginator {

    /**
     * Enum representing the possible outcomes of a move in the chess game.
     * - NORMAL: The move was successful and the game continues.
     * - FALHIDO: The move was invalid or failed.
     * - PROMOTION: A pawn has been promoted.
     * - CHECKMATE: The game has ended with a checkmate.
     * - STALEMATE: The game has ended in a stalemate.
     * - INSMATERIAL: The game has ended due to insufficient material to continue.
     */
    public enum acontecimentoMovimento {NORMAL, FALHIDO, PROMOTION, CHECKMATE, STALEMATE, INSMATERIAL}

    /**
     * Serial version UID for serialization compatibility.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The chess board on which the game is played.
     */
    private Board b;

    /**
     * The white player in the game.
     */
    private Player white;

    /**
     * The black player in the game.
     */
    private Player black;

    /**
     * Indicates whether the current player is white (true) or black (false).
     */
    boolean currentPlayer = true;// as brancas começam a jogar
    //private final List<JogadorAtualListener> jogadorAtualListeners = new ArrayList<>();

    /**
     * The name of the white player.
     */
    private String whitePlayerNome;

    /**
     * The name of the black player.
     */
    private String blackPlayerNome;

    /**
     * Indicates whether the game has ended in a draw.
     */
    boolean draw = false;

    /**
     * Indicates whether a piece was captured during the last move.
     */
    boolean captured = false;

    /**
     * Indicates whether the game is in checkmate.
     */
    boolean checkmate = false;

    /**
     * Constructs a ChessGame with specified player names.
     *
     * @param nomeBrancas The name of the white player.
     * @param nomePretas  The name of the black player.
     */
    public ChessGame(String nomeBrancas, String nomePretas) {
        this.b = new Board();
        this.whitePlayerNome = nomeBrancas;
        this.blackPlayerNome = nomePretas;
        this.white = new Player(true, whitePlayerNome, getPieces(currentPlayer));
        this.black = new Player(false, blackPlayerNome, getPieces(!currentPlayer));
        draw = false;
    }

    /**
     * Default constructor for ChessGame, initializes with default player names.
     */
    public ChessGame() {
        this("Jogador Brancas", "Jogador Pretas");
    }

    /**
     * Gets possible moves for a piece at the specified position.
     *
     * @param line   Row index
     * @param column Column index
     * @return List of move coordinates
     */
    public ArrayList<int[]> getMovesOfPiece(int line, int column) {
        return b.getMovesOfPiece(line, column);
    }

    /**
     * Attempts to perform a castling move.
     *
     * @param currentPlayer The current player
     * @param selected      First selected piece
     * @param target        Second selected piece
     * @return true if castling was successful
     */
    public boolean castle(boolean currentPlayer, Piece selected, Piece target) {
        Rook rook;
        King king;
        boolean condTiposPeca = !(selected.getType() == Board.type.KING && target.getType() == Board.type.ROOK || selected.getType() == Board.type.ROOK && target.getType() == Board.type.KING);
        boolean condColorPeca = selected.getColor() != target.getColor();
        if (condTiposPeca || condColorPeca) {
            return false;
        }
        if (selected.getType() == Board.type.ROOK) {
            rook = (Rook) selected;
            king = (King) target;
        } else {
            king = (King) selected;
            rook = (Rook) target;
        }
        var rookMoves = rook.getPossibleMoves();
        if (rookMoves.isEmpty()) {
            return false;
        } // o rook pode-se mover
        if (rook.getMoved() || king.getMoved()) {
            return false;
        }
        int rookColuna;
        int rookLinha = rook.getLine();
        int lim = rook.getColumn() == 0 ? 3 : 2;
        boolean condCasasLivres;
        for (int i = 1; i <= lim; i++) {
            rookColuna = rook.getColumn() == 0 ? rook.getColumn() + i : rook.getColumn() - i;
            condCasasLivres = isSquareAttacked(currentPlayer, rookLinha, rookColuna) || b.getPiece(rookLinha, rookColuna) != null;
            if (condCasasLivres) {
                return false;
            }
        }
        b.movePiecesForCastle(king, rook);
        changeCurrentPlayer();
        return true;
    }

    /**
     * Checks if a square is under attack by the opponent.
     *
     * @param currentPlayer The current player
     * @param line          Row index
     * @param column        Column index
     * @return true if the square is attacked
     */
    public boolean isSquareAttacked(boolean currentPlayer, int line, int column) {
        Player attacker = currentPlayer ? black : white;
        var attackerPieces = attacker.getPieces();
        for (var piece : attackerPieces) {
            var pieceMoves = piece.getPossibleMoves();
            for (var move : pieceMoves) {
                if (move[0] == line && move[1] == column) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the king is still under threat after a move.
     *
     * @param currentPlayer The current player
     * @param linha         Current row index of the piece
     * @param coluna        Current column index of the piece
     * @param novaLinha     New row index of the piece after the move
     * @param novaColuna    New column index of the piece after the move
     * @return true if the king is still under threat, false otherwise
     */
    public boolean kingStillUnderThreat(boolean currentPlayer, int linha, int coluna, int novaLinha, int novaColuna){
        Piece p = b.getPiece(linha, coluna);
        boolean kingUnderAttack;
        if(p == null)
            return false;
        if(!p.canMove(novaLinha, novaColuna)){ //Verifica se a peça pode se mover para aquela posi tendo em conta as regras
            return false;
        }

        Board stateBeforeMove = new Board(b.getBoard()); // Copia o estado da board (nao encontrei outra maneira)
        Piece k = stateBeforeMove.getKing(currentPlayer);
        stateBeforeMove.removePiece(linha, coluna);
        stateBeforeMove.putPiece(p,novaLinha, novaColuna);
        p.setPosicao(novaLinha, novaColuna, true);
        System.out.println(stateBeforeMove.isCheck(!currentPlayer));
        kingUnderAttack = stateBeforeMove.isUnderThreat(currentPlayer,k.getLine(),k.getColumn()); // verifica se ainda está a se atacado
        p.setPosicao(linha, coluna, true);
        return kingUnderAttack;
    }

    /**
     * Makes a move for a piece on the board.
     *
     * @param linha        Current row index of the piece
     * @param coluna       Current column index of the piece
     * @param novaLinha    New row index of the piece after the move
     * @param novaColuna   New column index of the piece after the move
     * @param currentPlayer The current player (true for white, false for black)
     * @return The moved Piece or null if the move is invalid
     */
    public Piece makeMove(int linha, int coluna, int novaLinha, int novaColuna, boolean currentPlayer){
        if(!b.posicaoValida(novaLinha, novaColuna) || !b.posicaoValida(linha, coluna))
            return null;
        Piece p;
        if((p = b.getPiece(linha, coluna)) == null)
            return null;
        if(p.getColor() != currentPlayer)
            return null; // não é a peça do jogador atual;
        if(!p.canMove(novaLinha, novaColuna)){ //Verifica se a peça pode se mover para aquela posi tendo em conta as regras
            return null;
        }
        if(!b.kingStillUnderThreat(currentPlayer,  linha,  coluna,  novaLinha,  novaColuna)){
            b.removePiece(linha, coluna);
            b.putPiece(p, novaLinha, novaColuna);
            p.setPosicao(novaLinha, novaColuna, false);
        }
        else{
            return null;
        }
        //System.out.println(castle(currentPlayer,p, novaLinha,novaColuna));

        return p;
    }

    /**
     * Moves a piece on the board and updates the game state.
     *
     * @param line    Current row index of the piece
     * @param col     Current column index of the piece
     * @param newLine New row index of the piece after the move
     * @param newCol  New column index of the piece after the move
     * @return The outcome of the move as an enum value.
     */
    public acontecimentoMovimento movePiece(int line, int col, int newLine, int newCol) {
        Piece selected = b.getPiece(line, col), target = b.getPiece(newLine, newCol);
        Piece targetEnPessant;
        if (selected != null && target != null) {
            System.out.println(castle(currentPlayer, selected, target) ? "Tentativa de castle" : "Tentativa falhida de castle");
        }
        if ((selected = makeMove(line, col, newLine, newCol, currentPlayer)) == null) {
            return acontecimentoMovimento.FALHIDO;// o movimento falhou
        }
        if (currentPlayer) {
            if (target != null) { // Se há uma peça na posição do movimento tira-a das peças disponiveis
                black.lostPiece(target);
                white.newCapture(target);
                captured = true;
            }
            white.removePawnsEnPessant();
            if (selected instanceof Pawn) {
                white.pawnMoved();
                if (!((Pawn) selected).getMoved())
                    ((Pawn) selected).setMoved(true);
                if (!((Pawn) selected).getMoved())
                    ((Pawn) selected).setMoved(true);

                if (((Pawn) selected).isEnPessant())
                    white.addPawnsEnPessant(selected);
                if ((targetEnPessant = b.getPiece(line, newCol)) != null && targetEnPessant.getColor() != selected.getColor() && targetEnPessant instanceof Pawn) { // en Pessant
                    black.lostPiece(targetEnPessant);
                    white.newCapture(targetEnPessant);
                    b.removePiece(line, newCol);
                }
            } else {
                white.pawnNotMoved();
            }

        } else {
            if (target != null) {
                white.lostPiece(target);
                black.newCapture(target);
            }
            black.removePawnsEnPessant();
            if (selected instanceof Pawn) {
                if (((Pawn) selected).isEnPessant())
                    black.addPawnsEnPessant(selected);
                if (!((Pawn) selected).getMoved())
                    ((Pawn) selected).setMoved(true);
                black.pawnMoved();
                if ((targetEnPessant = b.getPiece(line, newCol)) != null && targetEnPessant.getColor() != selected.getColor()) { // en Pessant
                    white.lostPiece(targetEnPessant);
                    black.newCapture(targetEnPessant);
                    b.removePiece(line, newCol);
                }
            } else {
                black.pawnNotMoved();
            }


        }
        setJoagdorAtual(!currentPlayer);
        System.out.println(determineGameState(!currentPlayer));
        if (isPromotion(!currentPlayer, newLine, newCol, selected)) {
            System.out.println("Promoção de peão!");
            return acontecimentoMovimento.PROMOTION; // Movimento válido e terminou o jogo
        }
        return determineGameState(!currentPlayer);
    }

    /**
     * Checks if a pawn is eligible for promotion.
     *
     * @param currentPlayer The current player
     * @param line          Row index
     * @param column        Column index
     * @param selected      The pawn piece
     * @return true if promotion is possible
     */
    public boolean isPromotion(boolean currentPlayer, int line, int column, Piece selected) {
        if (selected.getType() != Board.type.PAWN) {
            return false;
        }
        if (line == 0 && currentPlayer) {
            return true;

        } else return (line == 7 && !currentPlayer);
    }

    /**
     * Promotes a pawn to the specified piece type.
     *
     * @param currentPlayer The current player
     * @param piece         Piece type index (0-3: Q,R,B,N)
     * @return true if promotion was successful
     */
    public boolean changePawnPromotion(boolean currentPlayer, int piece) {
        int[] promoCord = getPromotingPawnCoord();
        char pieceId = switch (piece < 4 ? piece : piece - 4) {
            case 0 -> currentPlayer ? 'Q' : 'q';
            case 1 -> currentPlayer ? 'R' : 'r';
            case 2 -> currentPlayer ? 'B' : 'b';
            case 3 -> currentPlayer ? 'N' : 'n';
            default -> 'z';
        };
        if (pieceId == 'z') {
            return false;
        }
        createPieceAt(pieceId, promoCord[0], promoCord[1]);
        return true;
    }


    /**
     * Undo a move made in the game.
     *
     * @param line                Current row index of the piece
     * @param col                 Current column index of the piece
     * @param newLine             New row index of the piece after the move
     * @param newCol              New column index of the piece after the move
     * @param capturedPieceSymbol Symbol of the captured piece, if any
     */
    public acontecimentoMovimento undoMove(int line, int col, int newLine, int newCol, String capturedPieceSymbol) {
        // 1. Move a peça de volta sem validações
        Piece movedBack = b.movePieceForUndo(line, col, newLine, newCol);
        if (movedBack == null) {
            return acontecimentoMovimento.FALHIDO;
        }

        // 2. Restaura peça capturada se existir
        if (capturedPieceSymbol != null && !capturedPieceSymbol.isEmpty()) {
            char symbol = capturedPieceSymbol.charAt(0);
            boolean isWhite = Character.isUpperCase(symbol);
            Piece restored = Piece.createPiece(symbol, isWhite, line, col, b);
            if (restored == null || !b.addPiece(restored)) {
                return acontecimentoMovimento.FALHIDO;
            }

            if (isWhite) {
                white.newPiece(restored);
            } else {
                black.newPiece(restored);
            }
        }

        setJoagdorAtual(!currentPlayer);

        return acontecimentoMovimento.NORMAL;
    }

    /**
     * Redoes a move that was previously undone.
     *
     * @param linhaFinal    Final row index of the piece
     * @param colunaFinal   Final column index of the piece
     * @param linhaInicial  Initial row index of the piece before the move
     * @param colunaInicial Initial column index of the piece before the move
     * @param pecaCapturada Symbol of the captured piece, if any
     * @return The outcome of the redo operation as an enum value.
     */
    public acontecimentoMovimento redoMove(int linhaFinal, int colunaFinal, int linhaInicial, int colunaInicial, String pecaCapturada) {

        // 1. Move a peça de volta sem validações
        Piece movedBack = b.movePieceForUndo(linhaFinal, colunaFinal, linhaInicial, colunaInicial);
        if (movedBack == null) {
            return acontecimentoMovimento.FALHIDO;
        }

        // 2. Restaura peça capturada se existir
        if (pecaCapturada != null && !pecaCapturada.isEmpty()) {

            Piece destino = b.getPiece(linhaFinal, colunaFinal);
            if (destino != null && destino.getColor() != movedBack.getColor()) {
                b.removePiece(linhaFinal, colunaFinal);
                if (movedBack.getColor()) {
                    black.lostPiece(destino);
                    white.newCapture(destino);
                } else {
                    white.lostPiece(destino);
                    black.newCapture(destino);
                }
            }
        }

        setJoagdorAtual(!currentPlayer);

        return acontecimentoMovimento.NORMAL;
    }

/*    private void handlePawnMove(Pawn pawn, int oldLine, int newLine, int newCol) {
        if (currentPlayer) {
            white.pawnMoved();
        } else {
            black.pawnMoved();
        }

        if (!pawn.getMoved()) {
            pawn.setMoved(true);
        }

        if (pawn.isEnPessant()) {
            if (currentPlayer) {
                white.addPawnsEnPessant(pawn);
            } else {
                black.addPawnsEnPessant(pawn);
            }
        }
    }

    private void handleEnPassant(Pawn pawn, Piece targetEnPessant, int line, int newCol) {
        if (currentPlayer) {
            black.lostPiece(targetEnPessant);
            white.newCapture(targetEnPessant);
        } else {
            white.lostPiece(targetEnPessant);
            black.newCapture(targetEnPessant);
        }
        b.removePiece(line, newCol);
    }*/

    /**
     * Gets the coordinates of a pawn waiting for promotion.
     *
     * @return Array with [row, column] or null
     */
    public int[] getPromotingPawnCoord() {
        int line = !currentPlayer ? 0 : 7;
        int[] coordinates = new int[2];
        for (int i = 0; i < getBoardSize(); i++) {
            Piece p = b.getPiece(line, i);
            if (p == null)
                continue;
            if (p.getType() == Board.type.PAWN && p.getColor() == !currentPlayer) {
                coordinates[0] = p.getLine();
                coordinates[1] = p.getColumn();
                return coordinates;
            }
        }
        return null;
    }

    /**
     * Gets the name of the piece at the specified position.
     *
     * @param line   Row index
     * @param column Column index
     * @return Piece name or "empty"
     */
    public String getPieceName(int line, int column) {
        if (b.getPiece(line, column) == null)
            return "empty";
        Board.type type = b.getPiece(line, column).getType();
        return switch (type) {
            case PAWN -> "Pawn";
            case KNIGHT -> "Knight";
            case KING -> "King";
            case QUEEN -> "Queen";
            case ROOK -> "Rook";
            case BISHOP -> "Bishop";
        };
    }

    /**
     * Verifies if the piece with the following coordinates is an ally
     * @param line
     * @param column
     * @param color
     * @return true if it's an ally, false if it's otherwise */
    public boolean verifyAlly(int line, int column, boolean color){
        return color == b.getPiece(line, column).getColor();
    }

    /**
     * Gets the symbol of the piece at the specified position.
     *
     * @param l Row index
     * @param c Column index
     * @return Piece symbol or space
     */
    public String getPieceSimbolo(int l, int c) {
        Piece p = b.getPiece(l, c);
        if (p != null) {
            return p.getSimbolo();
        }
        return " ";
    }

    /**
     * Gets the pieces that each player has
     *
     * @param color true - white, false - black
     * @return List of Pieces
     */
    private ArrayList<Piece> getPieces(boolean color) {
        ArrayList<Piece> p = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //p.add(b.getPiece(i,j));
                if (b.getPiece(i, j) != null) {
                    if (b.getPiece(i, j).getColor() == color)
                        p.add(b.getPiece(i, j));
                }
            }
        }
        return p;
    }

    /**
     * Gets the current board status as a string.
     *
     * @return String representation of the board
     */
    public String getBoardStatus() {
        return b.toString();
    }

    /**
     * Gets a formatted string of the current board.
     *
     * @return Formatted board string
     */
    public String printBoardGame() {
        return b.printBoard();
    }

    /**
     * Gets possible moves for a piece as a formatted string.
     *
     * @param line Row index
     * @param col  Column index
     * @return Formatted moves string
     */
    public String getPieceMoves(int line, int col) {
        ArrayList<int[]> moves = b.getPiece(line, col).getPossibleMoves();
        StringBuilder sb = new StringBuilder();
        for (int[] move : moves) {
            sb.append(String.format("{%d,%d} ", move[0], move[1]));
        }
        return sb.toString();
    }

    /**
     * Changes the current player.
     */
    public void changeCurrentPlayer() {
        currentPlayer = !currentPlayer;
    }

    /**
     * Gets the current player.
     *
     * @return true for white, false for black
     */
    public boolean getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the king's position for the specified color.
     *
     * @param color true for white, false for black
     * @return Array with [row, column] coordinates
     */
    public int[] getKingPos(boolean color) {
        Piece king = b.getKing(color);
        if (king != null) {
            return new int[]{king.getLine(), king.getColumn()};
        }
        return null;
    }

    // ENDINGS
    /**
     * Determines the GameState.
     *
     * @param currentPlayer true for white, false for black
     * @return Enum acontecimentoMoviento for Game State.
     */
    public acontecimentoMovimento determineGameState(boolean currentPlayer) {
        Player victim = currentPlayer ? black : white;
        boolean temMovimentos = false;
        var victimPieces = victim.getPieces();
        loopFora:
        for (var piece : victimPieces) {
            var moves = piece.getPossibleMoves();
            for (var move : moves) {
                if (!b.kingStillUnderThreat(victim.getColor(), piece.getLine(), piece.getColumn(), move[0], move[1])) {
                    temMovimentos = true;
                    break loopFora;
                }
            }
        }
        if (temMovimentos) {
            if (insufficientMaterial()) {
                draw = true;
                return acontecimentoMovimento.INSMATERIAL;
            }
            return acontecimentoMovimento.NORMAL;
        }
        if (b.isCheck(currentPlayer) || b.isCheck(victim.getColor())) {
            checkmate = true;
            return acontecimentoMovimento.CHECKMATE;
        }
        draw = true;
        return acontecimentoMovimento.STALEMATE;
    }

    /**
     * Confirms that is insufficient material.
     *
     * @param color true for white, false for black
     * @return true if the player has insufficient material to checkmate
     */
    public boolean conForIM(boolean color) {
        Player player = color ? white : black;
        int nKnight = 0, nBishop = 0;
        for (var piece : player.getPieces()) {
            if (piece.getType() == Board.type.QUEEN || piece.getType() == Board.type.PAWN || piece.getType() == Board.type.ROOK) {
                return false;
            }
            if (piece.getType() == Board.type.KNIGHT) {
                nKnight++;
            }
            if (piece.getType() == Board.type.BISHOP) {
                nBishop++;
            }
        }
        if (nKnight > 1 || nBishop > 1) {
            return false;
        }
        return nKnight == 0 || nBishop == 0;
    }

    /**
     * Checks for insufficient material conditions.
     *
     * @return true if neither player has sufficient material to checkmate
     */
    public boolean insufficientMaterial() {
        boolean whiteIns = conForIM(true), blackIns = conForIM(false);
        return whiteIns && blackIns;
    }

    /**
     * Checks if the game is in checkmate for the current player.
     *
     * @param currentPlayer The current player
     * @param white         White player object
     * @param black         Black player object
     * @return true if checkmate, false otherwise
     */
    public boolean isCheckmate(boolean currentPlayer, Player white, Player black) {
        Player victim = currentPlayer ? black : white;
        if (!b.isCheck(victim.getColor())) {
            return false;
        }
        var victimPieces = victim.getPieces();
        for (var piece : victimPieces) {
            var moves = piece.getPossibleMoves();
            for (var move : moves) {
                if (!b.kingStillUnderThreat(victim.getColor(), piece.getLine(), piece.getColumn(), move[0], move[1])) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the game is in stalemate for the current player.
     *
     * @param currentPlayer The current player
     * @param player        Player object
     * @return true if stalemate, false otherwise
     */
    public boolean isStalemate(boolean currentPlayer, Player player) {
        Piece p = b.getKing(currentPlayer);
        if (p == null) {
            return false;
        }
        return p.getPossibleMoves().isEmpty() && !player.hasMoves();
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game has ended
     */
    public boolean isGameOver() {

        return determineGameState(!currentPlayer) != acontecimentoMovimento.NORMAL;
    }

    /**
     * Exports the current game state to a string.
     *
     * @return Game state string or null
     */
    public String exportGame() {
        StringBuilder sb = new StringBuilder();
        sb.append(currentPlayer ? "WHITE" : "BLACK");

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                Piece p = b.getPiece(i, j);
                if (p != null) {
                    sb.append(",").append(p.getPosicao());
                }
            }
        }

        return sb.toString();
    }

    /**
     * Imports a game state from a string.
     *
     * @param jogo  Game state string
     * @param nome1 Name of the white player
     * @param nome2 Name of the black player
     * @return true if import was successful, false otherwise
     */
    public boolean importGame(String jogo, String nome1, String nome2) {

        if (jogo == null || jogo.isEmpty()) return false;

        String[] partes = jogo.split(",");
        if (partes.length < 1) return false;

        for (int i = 0; i < b.getSize(); i++) {
            for (int j = 0; j < b.getSize(); j++) {
                b.removePiece(i, j);
            }
        }

        String jogadorAtual = partes[0].trim();
        if (jogadorAtual.equalsIgnoreCase("WHITE")) {
            currentPlayer = true;
        } else if (jogadorAtual.equalsIgnoreCase("BLACK")) {
            currentPlayer = false;
        } else {
            return false;
        }
        for (int i = 1; i < partes.length; i++) {
            String peca = partes[i].trim();

            if (peca.isEmpty()) continue;


            boolean hasMoved = peca.endsWith("*");
            if (hasMoved) {
                peca = peca.substring(0, peca.length() - 1); // Remove o *
            }

            Piece pecaNova = Piece.createPiece(peca, b);

            if (pecaNova != null) {
                b.addPiece(pecaNova);
                //  boolean hasMoved = !peca.endsWith("*");
                if (pecaNova instanceof Rook) {
                    ((Rook) pecaNova).setMoved(!hasMoved);
                } else if (pecaNova instanceof King) {
                    ((King) pecaNova).setMoved(!hasMoved);
                }
            }
        }

        this.whitePlayerNome = nome1;
        this.blackPlayerNome = nome2;
        this.white = new Player(true, nome1, getPieces(true));
        this.black = new Player(false, nome2, getPieces(false));

        return b.getKing(true) != null && b.getKing(false) != null; // Jogo inválido (falta um rei)
    }

    /**
     * Gets the white player's name.
     *
     * @return White player name
     */
    public String getWhitePlayerName() {
        return whitePlayerNome;
    }

    /**
     * Gets the black player's name.
     *
     * @return Black player name
     */
    public String getBlackPlayerName() {
        return blackPlayerNome;
    }

    /**
     * Gets the symbol of the piece at the specified position.
     *
     * @param linha  Row index
     * @param coluna Column index
     * @return Piece symbol or space
     */
    public char getSimboloPecaLocalizada(int linha, int coluna) {
        Piece p = b.getPiece(linha, coluna);
        if (p != null) {
            return p.getSimbolo().charAt(0);
        }
        return ' ';
    }

    /**
     * Checks if the piece at the position belongs to the current player.
     *
     * @param linha  Row index
     * @param coluna Column index
     * @return true if piece belongs to current player
     */
    public boolean isCurrentPlayerPiece(int linha, int coluna) {
        Piece p = b.getPiece(linha, coluna);
        if (p != null) {
            return p.getColor() == currentPlayer;
        }
        return false;
    }

    /**
     * Gets possible moves for a piece as a formatted string.
     *
     * @param p The piece to check
     * @return Formatted moves string
     */
    public String getPieceMovesOfPiece(Piece p) {
        int line = p.getLine(), column = p.getColumn();
        StringBuilder out = new StringBuilder(), cell = new StringBuilder();
        out.append("\nA peça poderá se mover para as seguintes células: \n");
        for (int[] move : p.getPossibleMoves()) {
            cell.append((char) (move[1] + 'a')).append(move[0]);
            out.append(cell).append(", ");
            cell.setLength(0);
        }
        out.setLength(out.length() - 2); // Remove a última vírgula e espaço
        return out.toString();
    }

    /**
     * Gets the board size.
     *
     * @return The board size (8 for standard chess)
     */
    public int getBoardSize() {
        return b.getSize();
    }


    /**
     * Sets the current player.
     *
     * @param joagdorAtual true for white, false for black
     */
    public void setJoagdorAtual(boolean joagdorAtual) {
        this.currentPlayer = joagdorAtual;
        //notifyJogadorAtualMudou();
    }


    // ver se o rei está em check através do board

    /**
     * Checks if the king of the specified color is in check.
     *
     * @param currentPlayer true for white, false for black
     * @return true if the king is in check
     */
    public boolean KingIsInCheck(boolean currentPlayer) {
        return b.isCheck(currentPlayer);
    }

    /**
     * Creates a piece at the specified position.
     *
     * @param symbol Piece symbol
     * @param line   Row index
     * @param col    Column index
     * @return The created piece or null
     */
    public Piece createPieceAt(char symbol, int line, int col) {
        Piece piece = Piece.createPiece(symbol, Character.isUpperCase(symbol), line, col, b);
        if (piece != null) {
            b.removePiece(line, col);
            b.addPiece(piece);
            if (currentPlayer) {
                white.newPiece(piece);
            } else {
                black.newPiece(piece);
            }
            return piece;
        }
        return null;
    }


    /**
     * Saves the current game state for memento pattern.
     *
     * @return Memento containing game state
     */
    @Override
    public IMemento save() {
        System.out.println("Saving game state...");
        System.out.println(printBoardGame());

        ChessGameState state = new ChessGameState(
                this.b.deepCopy(),
                this.currentPlayer,
                this.whitePlayerNome,
                this.blackPlayerNome,
                this.draw
        );
        return new Memento(state);
    }

    /**
     * Restores the game state from a memento.
     *
     * @param memento Memento containing game state
     */
    @Override
    public void restore(IMemento memento) {
        ChessGameState state = (ChessGameState) memento.getSnapshot();
        this.b = state.board.deepCopy(); // Usa deepCopy novamente para garantir independência
        this.currentPlayer = state.currentPlayer;
        this.whitePlayerNome = state.whitePlayerNome;
        this.blackPlayerNome = state.blackPlayerNome;
        this.draw = state.draw;

        // Reconstroi os jogadores com as peças atuais
        this.white = new Player(true, whitePlayerNome, getPieces(true));
        this.black = new Player(false, blackPlayerNome, getPieces(false));

        for (Piece piece : getPieces(true)) {
            piece.setBoard(this.b);
        }
        for (Piece piece : getPieces(false)) {
            piece.setBoard(this.b);
        }

    }

    /**
     * Creates a deep copy of the current game.
     *
     * @return Deep copy of the game
     */
    public ChessGame deepCopy() {
        ChessGame copy = new ChessGame(this.whitePlayerNome, this.blackPlayerNome);
        copy.b = this.b.deepCopy();  // Usa o deepCopy() do Board
        copy.white = new Player(true, this.whitePlayerNome, copy.getPieces(true));
        copy.black = new Player(false, this.blackPlayerNome, copy.getPieces(false));
        copy.currentPlayer = this.currentPlayer;
        copy.draw = this.draw;
        return copy;
    }

    /**
     * Checks if the game is a draw.
     *
     * @return true if the game is a draw
     */
    public boolean isDraw() {
        return draw;
    }

    /**
     * Gets the boolean for captured.
     *
     * @return true if there was a capture, false otherwise
     */
    public boolean getCaptured() {
        return captured;
    }

    /**
     * Sets the captured boolean to false.
     */
    public void setCapturedToFalse() {
        captured = false;
    }

    /**
     * Checks if the game is in checkmate.
     *
     * @return true if the game is in checkmate
     */
    public boolean isCheckmate() {
        return checkmate;
    }

    /**
     * Gets the column character for the chess board.
     *
     * @param coluna Column index
     * @return Character representing the column
     */
    public char coordTabuleiroColuna(int coluna) {
        if (coluna < 0 || coluna >= b.getSize()) {
            throw new IllegalArgumentException("Coluna inválida: " + coluna);
        }
        return (char) ('a' + coluna);
    }

    /**
     * Gets the row index for the chess board.
     *
     * @param linha Row index
     * @return Inverted row index for the chess board
     */
    public int coordTabuleiroLinha(int linha) {
        if (linha < 0 || linha >= b.getSize()) {
            throw new IllegalArgumentException("Linha inválida: " + linha);
        }
        return b.getSize() - linha; // Inverte a linha para o formato de tabuleiro
    }
}
