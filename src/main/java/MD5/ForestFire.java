package MD5;

import java.awt.*;
import java.util.HashMap;
import java.util.Vector;

public class ForestFire {

    private static final int tree = Color.GREEN.getRGB();
    private static final int burningTree = Color.ORANGE.getRGB();
    private static final int coal = Color.BLACK.getRGB();
    private static final int mountain = Color.DARK_GRAY.getRGB();

    private HashMap<String, Integer> checker(Vector<Integer> colorVector){
        HashMap<String, Integer> colorMap = new HashMap<>();
        int greenTree = 0;
        int fire = 0;
        for(int color: colorVector){
            if(color == tree) greenTree++;
            else if (color == burningTree) fire++;
        }
        colorMap.put("tree", greenTree);
        colorMap.put("fire", fire);
        return colorMap;
    }

}
