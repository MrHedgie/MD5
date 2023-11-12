package MD5;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class ForestFire {

    private static final int tree = Color.GREEN.getRGB();
    private static final int burningTree = Color.ORANGE.getRGB();
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
                    if (board.getRGB(x, y + 1) == burningTree) fireChance *= 1.2;
                    break;
                }catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "S":
                try {
                    if(board.getRGB(x, y-1) == burningTree) fireChance*=1.2;
                    break;
                }catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "E":
                try{
                    if (board.getRGB(x+1, y) == burningTree) fireChance*=1.2;
                    break;
                } catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
            case "W":
                try{
                    if(board.getRGB(x-1, y) == burningTree) fireChance*=1.2;
                    break;
                } catch (ArrayIndexOutOfBoundsException e){
                    break;
                }
        }
        return fireChance;
    }

    private void regrow(HashMap<String, Integer> colorMap, double baseRegrowChance, int x, int y) {
        double regrow = baseRegrowChance * 0.05 * colorMap.get("tree");
        double chance = ThreadLocalRandom.current().nextDouble(0, 1);
        if(regrow < chance) nextGen.setRGB(x, y, tree);
    }

    private void burn(HashMap<String, Integer> colorMap, double baseFireChance, int x, int y){
        if(colorMap.get("fire") > 0) {
            double burn = baseFireChance + 0.05 * colorMap.get("fire");
            double chance = ThreadLocalRandom.current().nextDouble(0, 1);
            if (burn < chance) nextGen.setRGB(x, y, burningTree);
            else nextGen.setRGB(x, y, tree);
        }
    }

    private void run(double baseFireChance, double dampness, double regrowChance, String windDirection) {
        for (int y = 0; y < board.getHeight() - 1; y++) {
            for (int x = 0; x < board.getWidth() - 1; x++) {
                HashMap<String, Integer> colorMap = absorbingBoundary(x, y);
                int pixelColor = board.getRGB(x, y);
                double fireChance = calculateFireChance(baseFireChance, colorMap, windDirection, x, y) - dampness;
                if(pixelColor == tree) burn(colorMap, fireChance, x, y);
                else if(pixelColor == coal) regrow(colorMap, regrowChance, x, y);
                else if (pixelColor == burningTree) nextGen.setRGB(x, y, coal);
            }
        }
        FileHandler.saveImage("1", nextGen);
    }

    public void logic() {
        board = FileHandler.openImage("src/main/resources/SmallForest.bmp");
        nextGen = FileHandler.openImage("src/main/resources/SmallForest.bmp");
        double fireChance = 0.5;
        double dampness = 0.1;
        double regrowChance = 0.5;
        String windDirection = "E";
        run(fireChance, dampness, regrowChance, windDirection);
    }

}
