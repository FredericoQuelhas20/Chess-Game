package pt.isec.pa.chess.model;


import pt.isec.pa.chess.model.data.ChessGameSerialization;
import pt.isec.pa.chess.model.memento.CareTaker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.ArrayList;

/**
 * The ChessGameManager class manages the chess game, including game state, player turns,
 * piece movements, and game persistence. It serves as the main controller between the
 * view and the ChessGame model.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Game initialization and state management</li>
 *   <li>Piece movements and validations</li>
 *   <li>Undo/redo functionality</li>
 *   <li>Game saving and loading</li>
 *   <li>Language and sound settings</li>
 *   <li>Event notifications to observers</li>
 * </ul>
 *
 * @see ChessGame
 * @see ModelLog
 */

public class ChessGameManager {

    /**
     * The ChessGame instance representing the current game state.
     */
    ChessGame chessGame;

    /**
     * PropertyChangeSupport instance for managing property change listeners.
     * This allows the ChessGameManager to notify observers about changes in the game state.
     */
    private final PropertyChangeSupport pcs;
    /**
     * ModelLog instance for logging game events and actions.
     * This provides a history of actions taken during the game.
     */
    private final ModelLog logs;
    /**
     * Indicates whether a pawn promotion is currently active.
     * This is used to manage the promotion of pawns to other pieces when they reach the opposite end of the board.
     */
    private boolean promotionState = false;
    /**
     * Property name constant for board changes.
     */
    public static final String PROP_BOARD = "board";
    /**
     * Property name constant for promotion events.
     */
    public static final String PROP_PROMOTION = "promotion";
    /**
     * Property name constant for current player changes.
     */
    public static final String PROP_CURRENT_PLAYER = "currentPlayer";
    /**
     * Property name constant for game over events.
     */
    public static final String PROP_GAME_OVER = "gameOver";
    /**
     * Property name constant for learning mode changes.
     */
    private boolean learningMode = false;
    /**
     * Indicates whether sounds are allowed in the game and their language.
     */
    private boolean AllowSounds, English;
    /**
     * CareTaker instance for managing game state history.
     */
    private final CareTaker careTaker;

    /**
     * Constructs a new ChessGameManager with default settings.
     */
    public ChessGameManager() {
        chessGame = new ChessGame();
        this.careTaker = new CareTaker(chessGame);
        pcs = new PropertyChangeSupport(this);
        logs = ModelLog.getInstance();
        logs.addLog("Novo jogo iniciado");
        AllowSounds = false;
        English = true;
    }

    /**
     * Checks if the application is using English language.
     * @return true if English is enabled, false otherwise
     */
    public boolean usingEnglish() {return English;}

    /**
     * Toggles the application language between English and another language.
     */
    public void changeLanguage() {English = !English;}

    /**
     * Gets the current sound status.
     * @return true if sounds are enabled, false otherwise
     */
    public boolean getSoundStatus() {return AllowSounds;}

    /**
     * Toggles the sound on/off.
     */
    public void toggleSounds(){AllowSounds = !AllowSounds;}

    /**
     * Gets the name of the piece at the specified position.
     * @param line The row index (0-7)
     * @param column The column index (0-7)
     * @return The name of the piece or "empty" if no piece exists
     */
    public String getPieceName(int line, int column) { return chessGame.getPieceName(line, column); }

    public boolean verifyAlly(int line, int column, boolean color) { return chessGame.verifyAlly(line, column, color); }

    public void addPCListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePCListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Adds a property change listener for a specific property.
     * @param propertyName The name of the property to listen to
     * @param listener The listener to add
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Starts a new game with the specified players.
     * @param jogador1 Name of the white player
     * @param jogador2 Name of the black player
     */
    public void novoGame(String jogador1, String jogador2) {
        chessGame = new ChessGame(jogador1, jogador2);
        careTaker.reset();
        logs.clearLogs();
        logs.addLog("Novo jogo iniciado com " + jogador1 + " e " + jogador2);
        AllowSounds = false;
        learningMode = false;
        pcs.firePropertyChange(PROP_BOARD, null, null);
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());
    }

    /**
     * Gets the size of the chess board.
     * @return The board size (8 for standard chess)
     */
    public int getBoardSize() {
        return chessGame.getBoardSize();
    }


    /**
     * Checks if learning mode is enabled.
     * @return true if learning mode is active
     */
    public boolean isLearningMode() {
        return learningMode;
    }

    /**
     * Enables or disables learning mode.
     * @param learningMode true to enable learning mode
     */
    public void setLearningMode(boolean learningMode) {
        this.learningMode = learningMode;
        logs.addLog("Modo de aprendizagem " + (learningMode ? "ativado" : "desativado"));
        pcs.firePropertyChange("learningMode", !learningMode, learningMode);
    }

    /**
     * Moves a piece from one position to another.
     * @param line Current row of the piece
     * @param col Current column of the piece
     * @param newLine Destination row
     * @param newCol Destination column
     * @return true if the move was successful
     */
    public boolean movePiece(int line, int col, int newLine, int newCol){
            String capturada = chessGame.getSimboloPecaLocalizada(newLine, newCol) != ' ' ?
                String.valueOf(chessGame.getSimboloPecaLocalizada(newLine, newCol)) :
                null;

        careTaker.save(chessGame.save());

        ChessGame.acontecimentoMovimento resultado = chessGame.movePiece(line, col, newLine, newCol);

        if(resultado == ChessGame.acontecimentoMovimento.NORMAL){
            //notifyEstadoJogoMudado();
            char Oldcol = chessGame.coordTabuleiroColuna(col);
            int Oldlinha = chessGame.coordTabuleiroLinha(line);
            char newColChar = chessGame.coordTabuleiroColuna(newCol);
            int newLinha = chessGame.coordTabuleiroLinha(newLine);
            logs.addLog("Jogador " + (!chessGame.getCurrentPlayer() ? "Branco" : "Preto") + " moveu " + chessGame.getPieceSimbolo(newLine, newCol) + " de (" + Oldcol + Oldlinha + ") para (" + newColChar + newLinha + ")");
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());
        }
        else if(resultado == ChessGame.acontecimentoMovimento.PROMOTION){
            logs.addLog("Jogador " + (!chessGame.getCurrentPlayer() ? "Branco" : "Preto") + " Promoveu " + chessGame.getPieceSimbolo(newLine, newCol) + " para ");
            changePromotionState();
        }else if(resultado == ChessGame.acontecimentoMovimento.FALHIDO)
            return false;
        else {
            handleGameEnd(resultado);
        }
        return true;
    }

    public void handleGameEnd(ChessGame.acontecimentoMovimento ending){
        if(ending == ChessGame.acontecimentoMovimento.CHECKMATE){
            logs.addLog("Fim de Jogo! Jogador " + (chessGame.getCurrentPlayer() ? "Preto" : "Branco") + " venceu!");
            pcs.firePropertyChange(PROP_GAME_OVER, null, null);
        } else if (ending == ChessGame.acontecimentoMovimento.STALEMATE) {
            logs.addLog("Fim de Jogo! Empate por Stalemate!");
            pcs.firePropertyChange(PROP_GAME_OVER, null, null);
        } else if (ending == ChessGame.acontecimentoMovimento.INSMATERIAL) {
            logs.addLog("Fim de Jogo! Empate por Insuficiência de Material!");
            pcs.firePropertyChange(PROP_GAME_OVER, null, null);
        }
        else return;
    }

    /**
     * Changes the current player to the next player.
     * This method toggles the current player and notifies observers of the change.
     */
    public void changeCurrentPlayer(){
        chessGame.changeCurrentPlayer();
        pcs.firePropertyChange("O jogador atual mudou de " + (chessGame.getCurrentPlayer() ? "Brancas" : "Pretas") + " para " + (chessGame.getCurrentPlayer() ? "Pretas" : "Brancas") + " para " + (chessGame.getCurrentPlayer() ? "Brancas" : "Pretas"), null, null);
    }

    /**
     * Gets the current player.
     * @return true for white player, false for black
     */
    public boolean getCurrentPlayer() {
        return chessGame.getCurrentPlayer();
    }

    /**
     * Checks if the game is over.
     * @return true if the game has ended
     */
    public boolean isGameOver() {
        return chessGame.isGameOver();
    }

    public String getBoardStatus() {
        return chessGame.getBoardStatus();
    }
    //Promotion things
    /**
     * Checks if a pawn promotion is in progress.
     * @return true if waiting for promotion choice
     */
    public boolean getPromotionState(){return promotionState;}

    /**
     * Toggles the promotion state.
     */
    public void changePromotionState(){
        promotionState = !promotionState;
        ChessGame.acontecimentoMovimento res = chessGame.determineGameState(getCurrentPlayer());
        if(res != ChessGame.acontecimentoMovimento.NORMAL && !promotionState){
            handleGameEnd(res);
        }
    }

    /**
     * Promotes a pawn to the specified piece type.
     *
     * @param currentPlayer The player whose pawn is promoting
     * @param piece         The piece type to promote to (0-3: Q,R,B,N)
     */
    public void changePawnPromotion(boolean currentPlayer, int piece){
        chessGame.changePawnPromotion(currentPlayer, piece);
    }

    public String printBoardGame() {
        return chessGame.printBoardGame();
    }

    public String getPieceMoves(int line, int col) {
        return chessGame.getPieceMoves(line, col);
    }

    /**
     * Gets possible moves for a piece as coordinates.
     * @param line Row of the piece
     * @param column Column of the piece
     * @return ArrayList of move coordinates
     */
    public ArrayList<int[]> getMovesOfPiece(int line, int column){
        return chessGame.getMovesOfPiece(line, column);
    }

    /**
     * Saves the current game to a file.
     * @param fich File path to save to
     * @return true if save was successful
     */
    public boolean saveJogo(String fich){
        try{
            ChessGameSerialization.serializeChessGame(chessGame, fich);
            logs.addLog("Jogo guardado com sucesso");

            return true;
        }catch (Exception e){
            logs.addLog("Erro ao guardar o jogo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves the current game to a file.
     *
     * @param fich File path to save to
     */
    public void openJogo(String fich){
        try{
            careTaker.reset();
            ChessGame jogoCarregado = ChessGameSerialization.desserialize(fich);
            if(jogoCarregado == null){
                return;
            }
            this.chessGame = jogoCarregado;

            logs.clearLogs();
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());

            chessGame.setJoagdorAtual(jogoCarregado.getCurrentPlayer());
            logs.addLog("Jogo aberto com sucesso");

        }catch (Exception e){
            logs.addLog("Erro ao abrir o jogo: " + e.getMessage());
        }
    }

    /**
     * Exports the current game state to a text file.
     *
     * @param fich File path to export to
     */
    public void exportGame(String fich){
        try{
            String paraExportar = chessGame.exportGame();
            if(paraExportar == null || paraExportar.isEmpty()){
                return;
            }


            try( FileWriter writer = new FileWriter(fich) ){
                writer.write(paraExportar);

                logs.addLog("Jogo exportado com sucesso");

            }

        } catch (Exception e) {
            logs.addLog("Erro ao exportar o jogo: " + e.getMessage());
        }
    }

    /**
     * Imports a game state from a text file.
     *
     * @param fich  File path to import from
     * @param nome1 Name for white player
     * @param nome2 Name for black player
     */
    public void importGame(String fich, String nome1, String nome2){
        try{
            careTaker.reset();
            File f = new File(fich);
            if(!f.exists() && !f.canRead()){
                return;
            }

            StringBuilder jogoImportado = new StringBuilder();

            try(BufferedReader reader = new BufferedReader(new FileReader(fich))){
                String linha;

                while((linha = reader.readLine()) != null){
                    jogoImportado.append(linha);
                }

            }

            logs.clearLogs();
            boolean res = chessGame.importGame(jogoImportado.toString(), nome1, nome2);
            pcs.firePropertyChange(PROP_BOARD, null, null);
            pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());
            logs.addLog("Jogo importado com sucesso");

        } catch (Exception e) {
            logs.addLog("Erro ao importar o jogo: " + e.getMessage());
        }
    }

    /**
     * Gets the name of the white player.
     * @return White player name
     */
    public String getNomeJogadorBrancas() {
        return chessGame.getWhitePlayerName();
    }

    /**
     * Checks if the king of the specified color is in check.
     * @param color true for white, false for black
     * @return true if the king is in check
     */
    public boolean KingInCheck(boolean color) {
        return chessGame.KingIsInCheck(color);
    }

    /**
     * Gets the name of the black player.
     * @return Black player name
     */
    public String getNomeJogadorPretas() {
        return chessGame.getBlackPlayerName();
    }

    /**
     * Gets the symbol of the piece located at the specified position.
     * @param linha Row index (0-7)
     * @param coluna Column index (0-7)
     * @return The symbol of the piece or ' ' if no piece exists
     */
    public char getSimboloPecaLocalizada(int linha, int coluna) {
        return chessGame.getSimboloPecaLocalizada(linha, coluna);
    }

    /**
     * Checks if the piece at the specified position belongs to the current player.
     * @param linha Row index (0-7)
     * @param coluna Column index (0-7)
     * @return true if the piece belongs to the current player
     */
    public boolean isCurrentPlayerPiece(int linha, int coluna) {
        return chessGame.isCurrentPlayerPiece(linha, coluna);
    }

    /**
     * Gets the coordinates of the pawn that is being promoted.
     * @return An array containing the row and column of the promoting pawn
     */
    public int[] getPromotingPawnCoord(){
        return chessGame.getPromotingPawnCoord();
    }

    /**
     * Gets the position of the king for the specified color.
     * @param color true for white, false for black
     * @return An array containing the row and column of the king
     */
    public int[] getKingPosition(boolean color) {
        return chessGame.getKingPos(color);
    }

    /**
     * Creates a piece at the specified position on the board.
     * @param simbolo The symbol of the piece to create
     * @param linha Row index (0-7)
     * @param coluna Column index (0-7)
     * @return true if the piece was created successfully
     */
    public boolean createPieceAt(char simbolo, int linha, int coluna) {
        if (chessGame.createPieceAt(simbolo, linha, coluna) != null) {
            pcs.firePropertyChange(PROP_BOARD, null, null);
            return true;
        }
        return false;
    }

    /**
     * Sets the current player for the chess game.
     * @param cor true for white, false for black
     */
    public void setJogadorAtual(boolean cor){
        chessGame.setJoagdorAtual(cor);
    }

    /**
     * Undoes the last move in the chess game.
     * This method restores the game state to the previous state before the last move.
     */
    public void undo(){
        if(!canUndo()) return;

        //careTaker.save(chessGame.save());
        System.out.println("UNDO - Estado anterior:");
        System.out.println(chessGame.printBoardGame());
        careTaker.undo();
        System.out.println("UNDO - Estado após undo:");
        System.out.println(chessGame.printBoardGame());
        pcs.firePropertyChange(PROP_BOARD, null, chessGame.getBoardStatus());
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());
    }

    /**
     * Redoes the last undone move in the chess game.
     * This method restores the game state to the state after the last undone move.
     */
    public void redo(){
        if (!canRedo()) return;

        //careTaker.save(chessGame.save()); // Salva estado atual antes do redo
        System.out.println("REDO - Estado anterior:");
        System.out.println(chessGame.printBoardGame());
        careTaker.redo();
        System.out.println("REDO - Estado após undo:");
        System.out.println(chessGame.printBoardGame());
        pcs.firePropertyChange(PROP_BOARD, null, chessGame.getBoardStatus());
        pcs.firePropertyChange(PROP_CURRENT_PLAYER, null, chessGame.getCurrentPlayer());
    }

    /**
     * Checks if an undo operation can be performed.
     * @return true if there are moves to undo
     */
    public boolean canUndo() {
        return careTaker.hasUndo();
    }

    /**
     * Checks if a redo operation can be performed.
     * @return true if there are moves to redo
     */
    public boolean canRedo() {
        return careTaker.hasRedo();
    }

    /**
     * Gets the current game state as a Memento object.
     * @return The current game state
     */
    public boolean getDraw(){
        return chessGame.isDraw();
    }

    /**
     * Checks if a piece has been captured.
     * @return true if a piece has been captured
     */
    public boolean getCaptured(){
        return chessGame.getCaptured();
    }

    /**
     * Sets the captured state to false.
     * This is used to reset the captured state after a piece has been captured.
     */
    public void setCapturedFalse(){
        chessGame.setCapturedToFalse();
    }

    /**
     * Checks if the game is in checkmate.
     * @return true if the game is in checkmate
     */
    public boolean getCheckmate(){
        return chessGame.isCheckmate();
    }

}
