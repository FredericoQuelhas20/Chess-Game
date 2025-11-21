package pt.isec.pa.chess.ui.res;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;

public class ImageManager {
    private ImageManager() {}
    private static final HashMap<String, Image> images = new HashMap<>();

    public static Image getImage(String imageName) {
        Image imagem = images.get(imageName);

        if(imagem == null){
            try(InputStream is = ImageManager.class.getResourceAsStream("images/pieces/" + imageName)){
                if(is != null){
                    imagem = new Image(is);
                    images.put(imageName, imagem);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error loading image: " + imageName, e);
            }
        }

        return imagem;
    }

    public static Image getExternalImage(String imageName) {
        Image imagem = images.get(imageName);
        if(imagem == null){
            try{
                imagem = new Image(imageName);
                images.put(imageName, imagem);
            } catch (Exception e) {
                return null;
            }
        }
        return imagem;
    }

    public static void purgeImage(String fich){
        images.remove(fich);
    }
}
