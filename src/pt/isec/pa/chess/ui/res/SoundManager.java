package pt.isec.pa.chess.ui.res;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.List;

public class SoundManager {
    private SoundManager(){}
    private static MediaPlayer reprodutor;

    public static void playSequence(List<String>sons, int index){
        try{

            if(index >= sons.size()){
                System.out.println(index + " indice - size " + sons.size());
                return;
            }

            var Sound = SoundManager.class.getResource("sounds/" + sons.get(index) +".mp3");
            assert Sound != null;
            String caminho = Sound.toExternalForm();
            Media media = new Media(caminho);
            reprodutor = new MediaPlayer(media);
            reprodutor.setStartTime(Duration.ZERO);

            if(sons.get(index).equals("br/br_captura") || sons.get(index).equals("en/capture")){
                reprodutor.setStartTime(Duration.seconds(1));
            }else if(sons.get(index).equals("br/br_preto") || sons.get(index).equals("en/black") || sons.get(index).equals("en/white") || sons.get(index).equals("br/br_branco")){
                reprodutor.setStartTime(Duration.ONE);
            }

            reprodutor.setAutoPlay(true);
            reprodutor.setOnEndOfMedia(() ->
                playSequence(sons, index+1)
            );
        }
        catch(Exception _){

        }
    }
    public static MediaPlayer getReprodutor(){
        return reprodutor;
    }
    public static boolean isPlaying(){
        return reprodutor != null && reprodutor.getStatus() == MediaPlayer.Status.PLAYING;
    }
    public static void Stop(){
        if(reprodutor != null && reprodutor.getStatus() == MediaPlayer.Status.PLAYING)
            reprodutor.stop();
    }

}
