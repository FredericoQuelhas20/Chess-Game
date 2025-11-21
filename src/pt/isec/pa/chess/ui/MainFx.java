package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;

public class MainFx extends Application {
    ChessGameManager chessGameManager;

    public MainFx(){
        this.chessGameManager = new ChessGameManager();
    }

    @Override
    public void start(Stage primaryStage) {
        ChessGameManager gameManager = new ChessGameManager();
        RootPane rootPane = new RootPane(gameManager, primaryStage);

        Scene scene = new Scene(rootPane, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess Game");
        primaryStage.show();

        // Show the log window
        JanelaLogs janelaLogs = new JanelaLogs(gameManager);
        janelaLogs.show();

        primaryStage.setOnCloseRequest(_ ->
            janelaLogs.close()
        );

    }

}
