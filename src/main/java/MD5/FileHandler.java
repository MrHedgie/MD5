package MD5;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public interface FileHandler {
    static BufferedImage openImage(String path){
        try{
            return ImageIO.read(new File(path));
        }catch (IOException e){
            System.out.println("File not found. Where did it go?");
        }
        return null;
    }

    static void saveImage(String name, BufferedImage image){
        try {
            File newImage = new File("src/main/java/images/" + name + ".bmp");
            ImageIO.write(image, "bmp", newImage);
        }catch (IOException e){
            System.out.println("File not created");
        }
    }
    static BufferedReader openText(String path){
        try{
            return new BufferedReader(new FileReader(path));
        }catch (IOException e){
            System.out.println("File not found. Where did it go?");
        }
        return null;
    }

}
