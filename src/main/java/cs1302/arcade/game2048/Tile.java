package cs1302.arcade.game2048;
import javafx.scene.image.*;

public class Tile extends ImageView {
    private int value;
    public int xIndex, yIndex;
    public boolean moved = false;
    public boolean merge = false;
    public boolean remove = false;
    
    /** Creates a new {@code Tile} and randomly sets its value to 2 or 4. */ 
    public Tile() {
        super();
        this.setFitWidth(100);
        this.setPreserveRatio(true);
        setValue((int)Math.pow(2, (int)((Math.random()*2) + 1)));
    }

    /**
     * Sets the value of the tile and changes its image to correspond ot the new value.
     * @param v the specified number that the tile's value will be set to
     */
    public void setValue(int v) {
        value = v;
        String file = "file:src/main/resources/2048tiles/" + v + ".png";
        this.setImage(new Image(file));
    }

    public int getValue() {
        return value;
    }
    
    public boolean canMerge(Tile t) {
        if(t != null && value == t.getValue()) {
            return true;
        }
        return false;
    }

    public int merge(Tile t) {
        if(canMerge(t)) {
            setValue(value * 2);
        }
        return value;
    }

    public void merge() {
        setValue(value * 2);
    }
}
