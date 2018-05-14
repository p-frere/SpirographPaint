import java.awt.*;
import java.awt.geom.Ellipse2D;
/**
 * Stores information about a drawn shape.
 * This includes the Ellipse2D object, the color and size
 */
public class Circle{
    private Integer brushSize;
    private Color brushColor;
    private Ellipse2D shape;            //Shape is either an Ellipse
    private boolean reflected;      //reflected flag for reflecting in a sector

    //getters and setters
    public Ellipse2D getShape() {
        return shape;
    }
    public void setShape(Ellipse2D shape) {
        this.shape = shape;
    }
    public Color getBrushColor() {
        return brushColor;
    }
    public void setBrushColor(Color brushColor) {
        this.brushColor = brushColor;
    }
    public boolean isReflected() {
        return reflected;
    }
    public void setReflected(boolean reflected) {
        this.reflected = reflected;
    }

    /** @return the center X cord of the circle */
    public Double getX(){
        return shape.getCenterX();
    }
    /** @return the center Y cord of the circle */
    public Double getY(){
        return shape.getCenterY();
    }

    /** @return the radius of the circle */
    public Double getRadius(){
        return shape.getHeight()/2;
    }

    /**
     * Returns a new circle object that has the same settings as the current except with a new Ellipse2D
     * @param shape new ellipse
     * @return circle object
     */
    public Circle copy(Ellipse2D shape){
        return new Circle(shape, this.brushColor, this.reflected);
    }

    /**
     * Creates a new Shape
     * @param shape line or circle object
     * @param brushColor color of line
     * @param reflected
     */
    public Circle(Ellipse2D shape, Color brushColor, boolean reflected){
        setShape(shape);
        setBrushColor(brushColor);
        setReflected(reflected);
    }
}
