package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import pt.isec.pa.chess.model.ChessGameManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;

public class ChessBoardInterface extends Canvas {
    private final ChessGameManager data;
    private int colunaSelecionada = -1;
    private int linhaSelecionada = -1;
    private final Label jogadorAtualLabel;
    ArrayList<int[]> movesOfPiece;
    private enum promotionPieces{
        QUEEN,
        ROOK,
        BISHOP,
        KNIGHT,
    }

    public ChessBoardInterface(ChessGameManager data, Label jogadorAtualLabel) {
        this.data = data;
        this.jogadorAtualLabel = jogadorAtualLabel;
        movesOfPiece = new ArrayList<>();
        //data.addJogadorAtualListener(this::updateJogadorAtual);

        data.addPropertyChangeListener(ChessGameManager.PROP_BOARD, _ -> draw());
        data.addPropertyChangeListener(ChessGameManager.PROP_CURRENT_PLAYER,
                evt -> updateJogadorAtual((boolean)evt.getNewValue()));
        data.addPropertyChangeListener(ChessGameManager.PROP_PROMOTION, _ -> data.changePromotionState());

        widthProperty().addListener(_ -> draw());
        heightProperty().addListener(_ -> draw());

        setOnMouseClicked(evento -> {
            if(data.isGameOver()) return;

            double tamanhoQuadrado = Math.min(getWidth(), getHeight()) / (data.getBoardSize() + 2);
            double startX = (getWidth() - (tamanhoQuadrado * data.getBoardSize())) / 2;
            double startY = (getHeight() - (tamanhoQuadrado * data.getBoardSize())) / 2;

            double mouseX = evento.getX() - startX;
            double mouseY = evento.getY() - startY;

            if((mouseX < 0 || mouseY < 0 ||
                    mouseX >= tamanhoQuadrado * data.getBoardSize() ||
                    mouseY >= tamanhoQuadrado * data.getBoardSize()) && !data.getPromotionState()) {
                return;
            }

            int coluna = (int) (mouseX / tamanhoQuadrado);
            int linha = (int) (mouseY / tamanhoQuadrado);

            if(data.getPromotionState()){
                if (linha != 0){
                    return;
                }
                data.changePawnPromotion(!data.getCurrentPlayer(), coluna);
                data.changePromotionState();
                draw();
            }
            else if(colunaSelecionada == -1 && linhaSelecionada == -1){
                if(data.isCurrentPlayerPiece(linha, coluna)){
                    colunaSelecionada = coluna;
                    linhaSelecionada = linha;
                    movesOfPiece = data.getMovesOfPiece(linhaSelecionada, colunaSelecionada);
                    for(int[] move : movesOfPiece){
                        System.out.println(Arrays.toString(move));
                    }

                    if(data.getSoundStatus()){
                        List<String> sons = new ArrayList<>();
                        String path;
                        if(data.usingEnglish()){
                            path = "en/";
                            if(data.KingInCheck(data.getCurrentPlayer()))
                                sons.add(path + "check");
                            sons.add(path + data.getPieceName(linha, coluna));
                            sons.add(path + (char) (coluna + 'a'));
                            sons.add(path + (8 - linha));
                            SoundManager.Stop();
                            SoundManager.playSequence(sons, 0);
                        }else{
                            path = "br/br_";
                            if(data.KingInCheck(data.getCurrentPlayer()))
                                sons.add(path + "xeque");

                            String peca = data.getPieceName(linha, coluna);
                            switch (peca) {
                                case "Bishop" -> sons.add(path  + "bispo");
                                case "Pawn" -> sons.add(path + "peao");
                                case "Knight" -> sons.add(path + "cavalo");
                                case  "King" -> sons.add(path + "rei");
                                case  "Queen" -> sons.add(path + "rainha");
                                case "Rook" -> sons.add(path + "torre");
                                case "Empty" -> sons.add(path + "empty");
                            }
                            sons.add(path + peca);

                            sons.add(path + (char) (coluna + 'a'));
                            sons.add(path + (8 - linha));
                            SoundManager.Stop();
                            SoundManager.playSequence(sons, 0);
                        }
                    }
                    draw();
                }
            } else {
                String hasEnemy = data.getPieceName(linha, coluna);
                if(data.movePiece(linhaSelecionada, colunaSelecionada, linha, coluna)){

                    if(data.getSoundStatus()){
                        List<String> sons = new ArrayList<>();
                        String path, player = data.getCurrentPlayer() ? "white" : "black";
                        if(data.usingEnglish()){
                            path = "en/";
                            if(!hasEnemy.equals("empty") && !data.verifyAlly(linha, coluna, data.getCurrentPlayer()))
                                sons.add(path + "capture");
                            sons.add(path + player);
                            if(data.KingInCheck(data.getCurrentPlayer()))
                                sons.add(path + "check");
                            if(data.getDraw())
                                sons.add(path + "draw");
                            else if(data.getCheckmate())
                                sons.add(path + "victory");
                            SoundManager.Stop();
                            SoundManager.playSequence(sons, 0);
                        }else{
                            path = "br/br_";
                            if(!hasEnemy.equals("empty") && !data.verifyAlly(linha, coluna, data.getCurrentPlayer()))
                                sons.add(path + "captura");
                            sons.add(path + (player == "white" ? "branca" : "preta"));
                            if(data.KingInCheck(data.getCurrentPlayer()))
                                sons.add(path + "xeque");
                            if(data.getDraw())
                                sons.add(path + "empate");
                            else if(data.getCheckmate())
                                sons.add(path + "vitoria");
                            SoundManager.Stop();
                            SoundManager.playSequence(sons, 0);
                        }
                    }
                    getParent().fireEvent(new ActionEvent());
                }
                colunaSelecionada = -1;
                linhaSelecionada = -1;
                movesOfPiece.clear();
                draw();
            }

        });

    }
    public void draw() {
        double cumprimento = getWidth();
        double altura = getHeight();
        double tamanhoQuadrado = Math.min(cumprimento, altura) / (data.getBoardSize() + 2 ) ;
        int [] promoCoord;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, cumprimento, altura);

        double startX = (cumprimento - (tamanhoQuadrado * data.getBoardSize())) / 2;
        double startY = (altura - (tamanhoQuadrado * data.getBoardSize())) / 2;

        for (int i = 0; i < data.getBoardSize(); i++){
            for(int j = 0; j < data.getBoardSize(); j++){
                if((i + j) % 2 == 0){
                    gc.setFill(Color.rgb(229, 93, 130, 0.8));
                } else {
                    gc.setFill(Color.rgb(64, 158, 219, 0.8));
                }

                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.0);
                gc.strokeRect(startX + j * tamanhoQuadrado, startY + i * tamanhoQuadrado,
                        tamanhoQuadrado, tamanhoQuadrado);

                if(i == (linhaSelecionada) && j == colunaSelecionada){
                    gc.setFill(Color.YELLOW);
                }
                if(data.isLearningMode() && colunaSelecionada != -1 && linhaSelecionada != -1){
                    for(int []  dir : movesOfPiece)
                        if(dir[0] == i && j == dir[1]){

                            double circleSize = tamanhoQuadrado * 0.4; // 40% do tamanho do quadrado
                            double circleX = startX + j * tamanhoQuadrado + (tamanhoQuadrado - circleSize)/2;
                            double circleY = startY + i * tamanhoQuadrado + (tamanhoQuadrado - circleSize)/2;

                            gc.setFill(Color.rgb(50, 205, 50, 0.6));
                            gc.fillOval(circleX, circleY, circleSize, circleSize);

                            gc.setStroke(Color.DARKGREEN);
                            gc.setLineWidth(1.5);
                            gc.strokeOval(circleX, circleY, circleSize, circleSize);
                        }
                }
                int [] KingPositon = data.getKingPosition(data.getCurrentPlayer());
                if(data.KingInCheck(data.getCurrentPlayer())){
                    if(i == KingPositon[0] && j == KingPositon[1]){
                        gc.setFill(Color.RED);
                    }
                }
                gc.fillRect(startX + j * tamanhoQuadrado, startY + i * tamanhoQuadrado, tamanhoQuadrado, tamanhoQuadrado);
                char simbolo = data.getSimboloPecaLocalizada((i), j);
                if(simbolo != ' '){
                    String imagem = getImagemPeca(simbolo);
                    Image img = ImageManager.getImage(imagem);
                    if(img != null){
                        double tamanho = tamanhoQuadrado * 0.8;
                        double x = (tamanhoQuadrado - tamanho) / 2;
                        gc.drawImage(img, startX + j * tamanhoQuadrado + x, startY + i * tamanhoQuadrado + x, tamanho, tamanho);
                    }
                }
            }
        }

        // Desenhar coordenadas das colunas (letras)
        gc.setFill(Color.BLACK);
        Font font = new Font("Arial", tamanhoQuadrado * 0.4);
        gc.setFont(font);
        for (int i = 0; i < data.getBoardSize(); i++) {
            String valorColuna = String.valueOf((char) ('a' + i));
            // Acima do tabuleiro
            gc.fillText(valorColuna, startX + i * tamanhoQuadrado + tamanhoQuadrado * 0.4, startY - tamanhoQuadrado * 0.3);
            // Abaixo do tabuleiro
            gc.fillText(valorColuna, startX + i * tamanhoQuadrado + tamanhoQuadrado * 0.4, startY + data.getBoardSize() * tamanhoQuadrado + tamanhoQuadrado * 0.8);
        }

        // Desenhar coordenadas das linhas (números)
        for (int i = 0; i < data.getBoardSize(); i++) {
            String valorLinha = String.valueOf(data.getBoardSize() - i);
            // À esquerda do tabuleiro
            gc.fillText(valorLinha, startX - tamanhoQuadrado * 0.8, startY + i * tamanhoQuadrado + tamanhoQuadrado * 0.6);
            // À direita do tabuleiro
            gc.fillText(valorLinha, startX + data.getBoardSize() * tamanhoQuadrado + tamanhoQuadrado * 0.3, startY + i * tamanhoQuadrado + tamanhoQuadrado * 0.6);
        }

        if (data.getPromotionState()) { // codigo para o menu para as promoçoes falta tornar a janela maior para poder ver
            promoCoord = data.getPromotingPawnCoord();
            double promotionY = startY - tamanhoQuadrado * 1.2;

            gc.fillRect(startX +  promoCoord[1] * tamanhoQuadrado - tamanhoQuadrado * 0.5,
                    promotionY,
                    tamanhoQuadrado * 5,
                    tamanhoQuadrado);

            for (int i = 0; i < promotionPieces.values().length; i++) {
                double menuSide = promoCoord[1] <= 4? 0:4;
                double promoX = startX + i * tamanhoQuadrado + tamanhoQuadrado * menuSide;
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(promoX, promotionY, tamanhoQuadrado, tamanhoQuadrado);

                String imgPath = getImagemPeca(promotionPieces.values()[i], promoCoord[0]==1); // se a linha de promoção é a de cima significa que é o branco a promover
                Image img = ImageManager.getImage(imgPath);
                if (img != null) {
                    double imgSize = tamanhoQuadrado * 0.8;
                    double offset = (tamanhoQuadrado - imgSize) / 2;
                    gc.drawImage(img, promoX + offset, promotionY + offset, imgSize, imgSize);
                }
            }
            //data.changePromotionState();
        }


    }

    private void updateJogadorAtual(boolean currentPlayer) {
        Platform.runLater(() -> {
            jogadorAtualLabel.setText("Jogador Atual: " + (currentPlayer ? "Brancas" : "Pretas"));
            draw();
        });
    }

    private String getImagemPeca(char simbolo) {
        String imagem = " ";
        boolean color = Character.isUpperCase(simbolo);

        imagem = switch (Character.toUpperCase(simbolo)) {
            case 'P' -> color ? "pawnW.png" : "pawnB.png";
            case 'R' -> color ? "rookW.png" : "rookB.png";
            case 'N' -> color ? "knightW.png" : "KnightB.png";
            case 'B' -> color ? "bishopW.png" : "bishopB.png";
            case 'Q' -> color ? "queenW.png" : "queenB.png";
            case 'K' -> color ? "kingW.png" : "kingB.png";
            default -> imagem;
        };

        return imagem;

    }
    private String getImagemPeca(promotionPieces simbolo, boolean color) {

        return switch (simbolo) {
            case promotionPieces.ROOK -> color ? "rookW.png" : "rookB.png";
            case promotionPieces.KNIGHT -> color ? "knightW.png" : "KnightB.png";
            case promotionPieces.BISHOP -> color ? "bishopW.png" : "bishopB.png";
            case promotionPieces.QUEEN -> color ? "queenW.png" : "queenB.png";
        };

    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return height;
    }

    @Override
    public double prefHeight(double width) {
        return width;
    }
}
