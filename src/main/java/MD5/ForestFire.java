package MD5;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.io.FileUtils;


public class ForestFire {

    private static final int tree = new Color(120,89,44).getRGB();
    private static final int burningTree = new Color(26, 81, 100).getRGB();
    private static final int coal = Color.BLACK.getRGB();
    //private static final int mountain = Color.DARK_GRAY.getRGB();
    private static BufferedImage board;
    private static BufferedImage nextGen;

    private HashMap<String, Integer> checker(Vector<Integer> colorVector) {
        HashMap<String, Integer> colorMap = new HashMap<>();
        int greenTree = 0;
        int fire = 0;
        for (int color : colorVector) {
            if (color == tree) greenTree++;
            else if (color == burningTree) fire++;
        }
        colorMap.put("tree", greenTree);
        colorMap.put("fire", fire);
        return colorMap;
    }


    private HashMap<String, Integer> absorbingBoundary(int x, int y) {
        Vector<Integer> colors = new Vector<>();
        for (int dy = -1; dy <= 1; dy++) {
            int newY = y + dy;
            for (int dx = -1; dx <= 1; dx++) {
                int newX = x + dx;
                try {
                    colors.add(board.getRGB(newX, newY));
                } catch (ArrayIndexOutOfBoundsException exception) {
                    colors.add(coal);
                }
            }
        }
        return checker(colors);
    }

    private double calculateFireChance(double baseFireChance, HashMap<String, Integer> colorMap, String windDirection, int x, int y){
        double fireChance = baseFireChance + 0.05 * colorMap.get("fire");
        switch (windDirection){
            case "N":
                try {
                    if (board.getRGB(x, y + 1) == burningTree) fireChance *= 1.5;
                    break;
                }catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "S":
                try {
                    if(board.getRGB(x, y-1) == burningTree) fireChance*=1.5;
                    break;
                }catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "E":
                try{
                    if (board.getRGB(x+1, y) == burningTree) fireChance*=1.5;
                    break;
                } catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "W":
                try{
                    if(board.getRGB(x-1, y) == burningTree) fireChance*=1.5;
                    break;
                } catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
        }
        return fireChance;
    }

    private void regrow(double baseRegrowChance, int x, int y) {
        double chance = ThreadLocalRandom.current().nextDouble(0, 1);
        if(chance < baseRegrowChance) nextGen.setRGB(x, y, tree);
    }

    private void burn(HashMap<String, Integer> colorMap, double baseFireChance, int x, int y){
        if(colorMap.get("fire") > 0) {
            double burn = baseFireChance + 0.05 * colorMap.get("fire");
            double chance = ThreadLocalRandom.current().nextDouble(0, 1);
            if (chance < burn) nextGen.setRGB(x, y, burningTree);
            else nextGen.setRGB(x, y, tree);
        }
    }

    private void run(double baseFireChance, double dampness, double regrowChance, String windDirection) {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                HashMap<String, Integer> colorMap = absorbingBoundary(x, y);
                int pixelColor = board.getRGB(x, y);
                double fireChance = calculateFireChance(baseFireChance, colorMap, windDirection, x, y) - dampness;
                if(pixelColor == tree) burn(colorMap, fireChance, x, y);
                else if(pixelColor == coal) regrow(regrowChance, x, y);
                else if (pixelColor == burningTree) nextGen.setRGB(x, y, coal);
            }
        }
    }

    public void logic() {
        double fireChance = 0.8;
        double dampness = 0.1;
        double regrowChance = 0.0;
        String windDirection = "E";
        try {
            FileUtils.cleanDirectory(new File("src/main/java/images"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        board = FileHandler.openImage("src/main/resources/ForestBigTest.png");
        nextGen = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < 10; i++) {
            run(fireChance, dampness, regrowChance, windDirection);
            FileHandler.saveImage(String.valueOf(i+1), nextGen);
            board = FileHandler.openImage("src/main/images/" + (i+1) + ".png");
        }
    }

}
