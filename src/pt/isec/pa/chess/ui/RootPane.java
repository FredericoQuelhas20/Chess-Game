package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;


import java.io.File;



public class RootPane extends BorderPane {
    ChessGameManager data;
    Stage stage;
    private final Label jogadorAtualLabel = new Label();
    private ChessBoardInterface chessBoardInterface;
    MenuBar menuBar;
    Menu game, mode;
    MenuItem New, open, save, Import, export, quit, undo, redo;
    CheckMenuItem normal, learning;
    Label jogadorBrancas, jogadorPretas, currentMode;
    Button toggleSounds, changeLanguage;

    public RootPane(ChessGameManager data, Stage stage) {
        this.data = data;
        this.stage = stage;
        createViews();
        registerHandlers();

        Platform.runLater(() -> {
            TextInputDialog jogador1 = new TextInputDialog("Jogador Brancas");
            jogador1.setTitle("Jogador Peças Brancas");
            jogador1.setHeaderText("Nome do Jogador das Peças Brancas");
            jogador1.setContentText("Nome do Jogador:");
            String nome1 = jogador1.showAndWait().orElse("");
            if (nome1.trim().isEmpty()) {
                nome1 = "Jogador1 (Brancas)";
            }

            TextInputDialog jogador2 = new TextInputDialog("Jogador Pretas");
            jogador2.setTitle("Jogador Peças Pretas");
            jogador2.setHeaderText("Nome do Jogador das Peças Pretas");
            jogador2.setContentText("Nome do Jogador:");
            String nome2 = jogador2.showAndWait().orElse("");
            if (nome2.trim().isEmpty()) {
                nome2 = "Jogador2 (Pretas)";
            }
            data.novoGame(nome1, nome2);
            update();
        });

        //this.addEventHandler(ActionEvent.ACTION, evento -> update());
    }

    private void createViews() {
        Font retroFont = Font.font("Courier New", FontWeight.BOLD, 12);
        this.setStyle("-fx-background-color: rgba(232, 189, 0, 0.7);");
        this.menuBar = new MenuBar();

        this.game = new Menu("Game");

        this.New = new MenuItem("New");
        this.open = new MenuItem("Open");
        this.save = new MenuItem("Save");
        this.Import = new MenuItem("Import");
        this.export = new MenuItem("Export");
        this.quit = new MenuItem("Quit");

        game.getItems().addAll(New, open, save, new SeparatorMenuItem(), Import, export, new SeparatorMenuItem(), quit);

        this.mode = new Menu("Mode");
        this.normal = new CheckMenuItem("Normal");
        this.learning = new CheckMenuItem("Learning");
        this.undo = new MenuItem("Undo");
        this.redo = new MenuItem("Redo");

        mode.getItems().addAll(normal, learning, new SeparatorMenuItem(), undo, redo);
        normal.setSelected(!data.isLearningMode());
        learning.setSelected(data.isLearningMode());
        undo.setDisable(true);
        redo.setDisable(true);
        toggleSounds = new Button("Turn On Sounds");
        toggleSounds.setFont(retroFont);
        changeLanguage = new Button("Change sound language");
        changeLanguage.setFont(retroFont);
        menuBar.getMenus().addAll(game, mode);

        game.setStyle("-fx-font-weight: bold;");
        mode.setStyle("-fx-font-weight: bold;");

        menuBar.setStyle("-fx-background-color: rgba(123, 101, 0, 0.7);");

        HBox informacaoJogadores = new HBox(20, toggleSounds, changeLanguage);
        informacaoJogadores.setPadding(new Insets(10));
        informacaoJogadores.setAlignment(Pos.CENTER);
        this.jogadorBrancas = new Label("Jogador Brancas: " + data.getNomeJogadorBrancas());
        this.jogadorPretas = new Label("Jogador Pretas: " + data.getNomeJogadorPretas());
        jogadorAtualLabel.setStyle("-fx-font-weight: bold");
        jogadorAtualLabel.setTextFill(Color.DARKBLUE);
        atualizarJogadorAtual(data.getCurrentPlayer());

        jogadorPretas.setFont(retroFont);
        jogadorBrancas.setFont(retroFont);
        jogadorAtualLabel.setFont(retroFont);

        informacaoJogadores.getChildren().addAll(jogadorBrancas, jogadorPretas, jogadorAtualLabel);

        this.currentMode = new Label("Current Mode : Normal");
        currentMode.setStyle("-fx-font-weight: bold;"
        + "-fx-border-width: 1px; "
        + "-fx-border-radius: 2px; "
        + "-fx-background-color: lightyellow; "
        + "-fx-background-radius: 2px;");
        currentMode.setTextFill(Color.DARKBLUE);
        HBox Mode = new HBox(20);
        Mode.setPadding(new Insets(10 , 0 , 0, 85));

        Mode.setAlignment(Pos.BASELINE_LEFT);
        Mode.getChildren().addAll(currentMode);

        StackPane sp = new StackPane();
        sp.setPadding(new Insets(20, 30, 20, 30));
        sp.setAlignment(Pos.CENTER);

        this.chessBoardInterface = new ChessBoardInterface(data, this.jogadorAtualLabel);
        chessBoardInterface.widthProperty().bind(sp.widthProperty().subtract(40));
        chessBoardInterface.heightProperty().bind(sp.heightProperty().subtract(40));

        sp.getChildren().add(chessBoardInterface);

        VBox vbox = new VBox(menuBar, informacaoJogadores, Mode);
        this.setTop(vbox);
        this.setCenter(sp);

    }

    public void registerHandlers() {

        data.addPropertyChangeListener(ChessGameManager.PROP_BOARD, _ -> update());
        data.addPropertyChangeListener(ChessGameManager.PROP_CURRENT_PLAYER, evt -> {
            boolean isWhite = (boolean) evt.getNewValue();
            Platform.runLater(() -> {
                atualizarJogadorAtual(isWhite);
                chessBoardInterface.draw();
            });
        });

        data.addPropertyChangeListener(ChessGameManager.PROP_GAME_OVER, _ -> {
        });


        New.setOnAction(_ -> {
            TextInputDialog jogador1 = new TextInputDialog("Jogador Brancas");
            jogador1.setTitle("Jogador Peças Brancas");
            jogador1.setHeaderText("Nome do Jogador das Peças Brancas");
            jogador1.setContentText("Nome do Jogador:");
            String nome1 = jogador1.showAndWait().orElse("");
            // nome padrão para o jogador branco
            if (nome1.trim().isEmpty()) {
                nome1 = "Jogador1 (Brancas)";
            }
            TextInputDialog jogador2 = new TextInputDialog("Jogador Pretas");
            jogador2.setTitle("Jogador Peças Brancas");
            jogador2.setHeaderText("Nome do Jogador das Peças Pretas");
            jogador2.setContentText("Nome do Jogador:");
            String nome2 = jogador2.showAndWait().orElse("");
            if (nome2.trim().isEmpty()) {
                nome2 = "Jogador2 (Pretas)";
            }
            data.novoGame(nome1, nome2);
            atualizarJogadorAtual(true);

            //atualizar novamente para o modo Normal
            normal.setSelected(!data.isLearningMode());
            learning.setSelected(data.isLearningMode());
            chessBoardInterface.draw();
        });

        open.setOnAction(_ -> {
            FileChooser f = new FileChooser();
            f.getExtensionFilters().add(new FileChooser.ExtensionFilter("JogoGuardado", "*.jogo"));

            File fich = f.showOpenDialog(stage);
            if (fich != null) {
                data.openJogo(fich.getAbsolutePath());
            }
        });

        save.setOnAction(_ -> {
            FileChooser f = new FileChooser();
            f.getExtensionFilters().add(new FileChooser.ExtensionFilter("JogoGuardado", "*.jogo"));

            File fich = f.showSaveDialog(stage);
            if (fich != null) {
                data.saveJogo(fich.getAbsolutePath());
            }
        });

        Import.setOnAction(_ -> {

            FileChooser f = new FileChooser();
            f.getExtensionFilters().add(new FileChooser.ExtensionFilter("JogoImportado", "*.txt", "*.csv"));
            File fich = f.showOpenDialog(stage);
            if (fich != null) {
                TextInputDialog jogador1 = new TextInputDialog("Jogador Brancas");
                jogador1.setTitle("Jogador Peças Brancas");
                jogador1.setHeaderText("Nome do Jogador das Peças Brancas");
                jogador1.setContentText("Nome do Jogador:");
                String nome1 = jogador1.showAndWait().orElse("");
                // nome padrão para o jogador branco
                if (nome1.trim().isEmpty()) {
                    nome1 = "Jogador1 (Brancas)";
                }
                TextInputDialog jogador2 = new TextInputDialog("Jogador Pretas");
                jogador2.setTitle("Jogador Peças Brancas");
                jogador2.setHeaderText("Nome do Jogador das Peças Pretas");
                jogador2.setContentText("Nome do Jogador:");
                String nome2 = jogador2.showAndWait().orElse("");
                if (nome2.trim().isEmpty()) {
                    nome2 = "Jogador2 (Pretas)";
                }
                data.importGame(fich.getAbsolutePath(), nome1, nome2);
            }
        });

        export.setOnAction(_ -> {
            FileChooser f = new FileChooser();
            f.getExtensionFilters().add(new FileChooser.ExtensionFilter("JogoExportado", "*.txt", "*.csv"));
            File fich = f.showSaveDialog(stage);
            if (fich != null) {
                data.exportGame(fich.getAbsolutePath());
            }
        });

        quit.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Sair");
            alert.setHeaderText(null);
            alert.setContentText("Queres mesmo sair desta app perfeita?");

            ButtonType Yes = new ButtonType("Sim");
            ButtonType No = new ButtonType("Não");

            alert.getButtonTypes().setAll(Yes, No);

            alert.showAndWait().ifPresent(response -> {
                if (response == Yes) {
                    //stage.close();
                    Platform.exit();
                }
            });
        });

        normal.setOnAction(_ -> {
            if(normal.isSelected()){
                learning.setSelected(false);
                data.setLearningMode(false);
                undo.setDisable(true);
                redo.setDisable(true);
            }else{
                normal.setSelected(true);
                undo.setDisable(true);
                redo.setDisable(true);
            }
            update();
        });

        learning.setOnAction(_ -> {
            if(learning.isSelected()){
                normal.setSelected(false);
                data.setLearningMode(true);

                undo.setDisable(false);
                redo.setDisable(false);
            }else{
                learning.setSelected(true);
            }
            update();
        });

        undo.setOnAction(_ -> {

            data.undo();
            chessBoardInterface.draw();
            update();
        });

        redo.setOnAction(_ -> {

            data.redo();
            chessBoardInterface.draw();
            update();
        });

        toggleSounds.setOnAction(_ -> {
           data.toggleSounds();
           update();
        });
        changeLanguage.setOnAction(_ -> {
            data.changeLanguage();
            update();
        });


    }

    public void update() {
        jogadorAtualLabel.setText("Jogador Atual: " + (data.getCurrentPlayer() ? "Brancas" : "Pretas"));
        jogadorBrancas.setText("Jogador Brancas: " + data.getNomeJogadorBrancas());
        jogadorPretas.setText("Jogador Pretas: " + data.getNomeJogadorPretas());
        if(data.isLearningMode())
            currentMode.setText("Current mode : Learning");
        else
            currentMode.setText("Current mode : Normal");
        if(data.getSoundStatus())
            toggleSounds.setText("Toggle Of Sounds");
        else
            toggleSounds.setText("Toggle On Sounds");
        if(data.usingEnglish())
            changeLanguage.setText("Change sound language");
        else
            changeLanguage.setText("Mudar o idioma do som");
        chessBoardInterface.draw();
        if(normal.isSelected()){
            undo.setDisable(true);
            redo.setDisable(true);
        }

    }

    private void atualizarJogadorAtual(boolean currentPlayer) {
        String jogador = currentPlayer ? data.getNomeJogadorBrancas() : data.getNomeJogadorPretas();
        String cor = currentPlayer ? "Brancas" : "Pretas";
        jogadorAtualLabel.setText("Jogador Atual: " + jogador + " (" + cor + ")");

        jogadorBrancas.setText("Jogador Brancas: " + data.getNomeJogadorBrancas());
        jogadorPretas.setText("Jogador Pretas: " + data.getNomeJogadorPretas());

    }


}

