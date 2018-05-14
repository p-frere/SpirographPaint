import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class Canvas extends JPanel{
    private BufferedImage image;                                    //The image is the picture that will show
    private Graphics2D g2;                                          // used to draw with
    private int x, y, oldX, oldY, brushSize, centerX, centerY;      //cords and brush size
    private int lineCount;                                          //how many Lines there are dividing the canvas into secotrs
    private boolean reflected;                                      //flags if the canvas is to be reflected
    private Color brushColor;                                       //color
    private Color tempColor;                                        //used to store color while eraser is in use
    private boolean eraseMode;                                      //Flags if eraser is active

    private ArrayList<ArrayList<Circle>> design = new ArrayList<>();//Stores all the Circles drawn in groups of strokes. The master list
    private Stack<ArrayList<Circle>> redoStack = new Stack<>();     //Stores recently undone Circles
    private ArrayList<Circle> strokeGroup;                          //Groups together Circles into a move that can be undone easily
    private UI ui;                                                  //Reference to the UI class Canvas is attached to

    /**
     * Sets size of the paint brush
     * @param brushSize
     */
    public void setBrushSize(Integer brushSize){
        this.brushSize = brushSize;
    }

    /**
     * Sets the color of the brush
     * @param brushColor
     */
    public void setBrushColor(Color brushColor) {
        g2.setColor(brushColor);
        this.brushColor = brushColor;
    }

    /**
     * Creates a new canvas
     * Sets default values and listeners
     * @param ui user interface attached
     */
    public Canvas(UI ui) {
        //sets up canvas
        this.ui = ui;
        this.setSize(600,600);
        centerX = this.getWidth()/2;
        centerY = this.getHeight()/2;
        image = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
        g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        setDoubleBuffered(false);
        newStokeGroup();

        //sets defaults settings
        setBrushSize(9);
        setBrushColor(Color.black);
        reflected = false;
        clearCanvas();
        setLineCount(1);
        eraseMode = false;

        //Listeners
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clearRedo();
                g2.setColor(brushColor);
                //adds a circle to canvas
                oldX = e.getX();
                oldY = e.getY();

                strokeGroup.add(new Circle(new Ellipse2D.Double(oldX-brushSize/2, oldY-brushSize/2, brushSize, brushSize), brushColor, reflected));

                drawSectors(strokeGroup.get(strokeGroup.size()-1));
                repaint();
            }
        });

       addMouseListener(new MouseAdapter() {
           public void mouseReleased(MouseEvent e) {
               //marks the end of a move and a group of Circles
               if (eraseMode)
                   removeFromDesign(strokeGroup);
               else
                   addToDesign(strokeGroup);
               newStokeGroup();
           }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                //adds a line to canvas
                x = e.getX();
                y = e.getY();
                strokeGroup.add(new Circle(new Ellipse2D.Double(oldX-brushSize/2, oldY-brushSize/2, brushSize, brushSize), brushColor, reflected));

                drawSectors(strokeGroup.get(strokeGroup.size()-1));
                repaint();
                oldX = x;
                oldY = y;
            }
        });
    }

    /**
     * Takes a Circle and draws a repeat and reflection over sectors
     * @param circle
     */
    public void drawSectors(Circle circle){
        if(lineCount != 0) {
            //rotates each item around each sector
            double incrAngle = (Math.PI*2 / lineCount);
            for (int i = 1; i <= lineCount; i++) {
                AffineTransform rotateLine = AffineTransform.getRotateInstance(i * incrAngle, centerX, centerY);
                g2.fill(((rotateLine.createTransformedShape(circle.getShape()))));

                if (circle.isReflected()) {
                    //Translates to the x-axis, reflects over the x-axis and translates back
                    AffineTransform reflectLine = AffineTransform.getRotateInstance(i * incrAngle, centerX, centerY);
                    reflectLine.translate(centerX, 0);
                    reflectLine.scale(-1, 1);
                    reflectLine.translate(-centerX, 0);
                    g2.fill(((reflectLine.createTransformedShape(circle.getShape()))));
                }
            }
        }
    }

    /** Updates the repetition of the pattern when the sector count changes */
    public void updateSectors(){
        clearCanvas();
        //for every stored Circle...
        for (ArrayList<Circle> strokeGroup : design){
            for(Circle circle : strokeGroup) {
                //...draw the their shape in their assigned color and size
                g2.setColor(circle.getBrushColor());
                drawSectors(circle);
            }
        }
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    /**Updates the line count for the sectors
     * @param lineCount number of repeats
     */
    public void setLineCount(Integer lineCount){
        this.lineCount = lineCount;
        updateSectors();
    }

    /**
     *Sets the reflector flag
     * @param reflected boolean flag
     */
    public void setReflected(boolean reflected){
        this.reflected = reflected;
    }

    /**clears the canvas and the stack of Circles */
    public void clear() {
        design.clear();
        clearCanvas();
    }

    /** Clears the canvas of paint */
    public void clearCanvas(){
        Color brushColour = g2.getColor();
        g2.setPaint(Color.white);

        // draw white on entire draw area to clear
        g2.fillRect(0, 0, getSize().width, getSize().height);
        g2.setPaint(brushColour);
        repaint();
    }

    /**
     * Selects an eraser
     * @param selected eraser flag
     */
    public void eraser(boolean selected){
        if (selected){
            eraseMode = true;
            tempColor = g2.getColor();
            setBrushColor(Color.WHITE);
        } else {
            eraseMode = false;
            setBrushColor(tempColor);
        }
    }

    /** creates a new collection of lines that will be undone if undo is pressed */
    public void newStokeGroup(){
        strokeGroup = new ArrayList<>();
    }

    /** Saves the current image to the undo stack */
    public void addToDesign(ArrayList<Circle> strokeGroup){
        design.add(strokeGroup);
    }

    /**
     * Removes points that have been selected by the erase tool
     * @param strokeGroup
     */
    public void  removeFromDesign(ArrayList<Circle> strokeGroup){
        //For all Circles in design...
        strokeGroup = duplicateSectors(strokeGroup);
        for (ArrayList<Circle> strokeSet : design){
            Iterator<Circle> circleItr = strokeSet.iterator();
            while(circleItr.hasNext()) {
                Circle circle = circleItr.next();
                for (Circle eraseCircle : strokeGroup){
                    //measures the square of the distance between the centres of the circles to see if they over lap
                    if ((eraseCircle.getX() - circle.getX()) * (eraseCircle.getX() - circle.getX()) + (eraseCircle.getY() - circle.getY()) * (eraseCircle.getY() - circle.getY()) <=
                            ((circle.getRadius() + eraseCircle.getRadius()) * (circle.getRadius() + eraseCircle.getRadius()))) {
                        //removes the circles
                        circleItr.remove();
                        break;
                    }
                }
            }
        }
        updateSectors();
    }

    /**
     * Instead of drawing the same line many times but rotated and reflected, this method adds
     * the rotations and reflections to the array list as physical circle objects
     * @param strokeGroup group of circles to be duplicated then rotated
     * @return new group with additional circles
     */
    public ArrayList<Circle> duplicateSectors(ArrayList<Circle> strokeGroup){
        ArrayList<Circle> circlesToAdd = new ArrayList<>();
        Iterator<Circle> circleItr = strokeGroup.iterator();
        while (circleItr.hasNext()) {
            Circle circle = circleItr.next();
            if (lineCount != 0) {
                //rotates each item around each sector
                double incrAngle = (Math.PI * 2 / lineCount);
                for (int i = 1; i <= lineCount; i++) {
                    AffineTransform rotateLine = AffineTransform.getRotateInstance(i * incrAngle, centerX, centerY);
                    //Creates a new shape from the transformed old shape
                    Shape newShape = rotateLine.createTransformedShape(circle.getShape());
                    //makes the shape into a new copy of circle and adds to the array list
                    circlesToAdd.add(circle.copy(new Ellipse2D.Double(newShape.getBounds().x, newShape.getBounds().y, newShape.getBounds().height, newShape.getBounds().width)));
                    if (circle.isReflected()) {
                        //Translates to the x-axis, reflects over the x-axis and translates back
                        AffineTransform reflectLine = AffineTransform.getRotateInstance(i * incrAngle, centerX, centerY);
                        reflectLine.translate(centerX, 0);
                        reflectLine.scale(-1, 1);
                        reflectLine.translate(-centerX, 0);
                        newShape = reflectLine.createTransformedShape(circle.getShape());
                        circlesToAdd.add(circle.copy(new Ellipse2D.Double(newShape.getBounds().x, newShape.getBounds().y, newShape.getBounds().height, newShape.getBounds().width)));
                    }
                }
            }
        }
        strokeGroup.addAll(circlesToAdd);
        return strokeGroup;
    }

    /** Recovers previous paintings from the UndoStack and displays them */
    public void undo(){
        if(!design.isEmpty()) {
            ArrayList<Circle> toUndo = design.get(design.size()-1); //gets last added element
            design.remove(design.size()-1); //removes it
            //pushes Circle group to redo stack
            redoStack.push(toUndo);
            updateSectors();
        }
    }

    /** Recovers previous paintings from the redo Stack and displays them */
    public void redo(){
        if (!redoStack.empty()) {
            ArrayList<Circle> toRedo = redoStack.pop();
            design.add(toRedo);
            updateSectors();
        }
    }

    /**Emptys the redo arraylist */
    public void clearRedo(){
        redoStack.clear();
    }

    /**Saves the current image to gallery*/
    public void save(){
        BufferedImage imageToSave = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = imageToSave.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        ui.addImage(imageToSave);
    }

}
