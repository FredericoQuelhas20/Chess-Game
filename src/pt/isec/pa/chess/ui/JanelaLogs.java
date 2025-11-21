package pt.isec.pa.chess.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JanelaLogs extends Stage implements PropertyChangeListener {
    private final ListView<String> lv;
    private final ObservableList<String> logs;
    private final ChessGameManager data;
    private final Label jogadorAtualLabel = new Label();
    private Button toggleSounds;
    private Button changeLanguage;

    public JanelaLogs(ChessGameManager data) {
        this.data = data;
        logs = FXCollections.observableArrayList();
        lv = new ListView<>(logs);

        createview();
        // Configura os handlers dos botões
        registerHandlers();
    }

    private void createview(){
        jogadorAtualLabel.setText("Jogador Atual: " + (data.getCurrentPlayer() ? "Brancas" : "Pretas"));
        jogadorAtualLabel.setStyle("-fx-font-weight: bold");
        jogadorAtualLabel.setTextFill(javafx.scene.paint.Color.DARKBLUE);

        toggleSounds = new Button(data.getSoundStatus() ? "Turn Off Sounds" : "Turn On Sounds");
        changeLanguage = new Button(data.usingEnglish() ? "Mudar o idioma do som" : "Change sound language");

        Font retroFont = Font.font("Courier New", FontWeight.BOLD, 12);
        jogadorAtualLabel.setFont(retroFont);
        toggleSounds.setFont(retroFont);
        changeLanguage.setFont(retroFont);

        Button btnClear = new Button("Limpar Histórico");
        btnClear.setOnAction(_ -> ModelLog.getInstance().clearLogs());

        // Layout para os botões e labels
        HBox infoBox = new HBox(10, jogadorAtualLabel, toggleSounds, changeLanguage);
        infoBox.setPadding(new Insets(10));
        infoBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(infoBox, lv, btnClear);
        Scene scene = new Scene(root, 600, 400); // Aumentei a largura para caber todos os elementos
        setScene(scene);
        setTitle("Movimentos do Jogo");

        ModelLog.getInstance().addPCListener(this);
        logs.addAll(ModelLog.getInstance().getLogs());
    }

    private void registerHandlers() {
        toggleSounds.setOnAction(_ -> {
            data.toggleSounds();
            toggleSounds.setText(data.getSoundStatus() ? "Turn Off Sounds" : "Turn On Sounds");
        });

        changeLanguage.setOnAction(_ -> {
            data.changeLanguage();
            changeLanguage.setText(data.usingEnglish() ? "Mudar o idioma do som" : "Change sound language");
        });

        // Atualiza os labels quando o jogador atual muda
        data.addPropertyChangeListener(ChessGameManager.PROP_CURRENT_PLAYER, evt -> {
            boolean isWhite = (boolean) evt.getNewValue();
            javafx.application.Platform.runLater(() ->
                jogadorAtualLabel.setText("Jogador Atual: " + (isWhite ? "Brancas" : "Pretas"))
            );
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("log".equals(evt.getPropertyName())) {
            javafx.application.Platform.runLater(() ->
                    logs.add(evt.getNewValue().toString())
            );
        } else if ("LimparLogs".equals(evt.getPropertyName())) {
            javafx.application.Platform.runLater(logs::clear);
        }
    }
}