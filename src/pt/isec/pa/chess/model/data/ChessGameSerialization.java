package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.ChessGame;

import java.io.*;

public class ChessGameSerialization {
    private ChessGameSerialization(){}

    public static void serializeChessGame(ChessGame game, String fich){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fich))){
          oos.writeObject(game);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChessGame desserialize(String fich){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fich))){
            return (ChessGame) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
