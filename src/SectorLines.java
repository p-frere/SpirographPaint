import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

/**
 * A panel that sits directly above the canvas
 * It is used to draw the sector lines
 */
public class SectorLines extends JPanel{
    private Integer lineCount;           //How many lines are drawn on the panel eg 1 line divides the panel twice
    private Integer centerX, centerY;

    /** Creates a new Panel to draw lines on */
    SectorLines(){
        setSize(600,600);
        lineCount = 0;
        centerX = getWidth()/2;
        centerY = getHeight()/2;
        repaint();
    }

    /**Updates the line count to desired number and repaints */
    public void setLineCount(Integer lineCount){
        this.lineCount = lineCount;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        //Sets to transparent so canvas can be seen underneath
        setOpaque(false);
        super.paintComponent(g);
        removeAll();
        Graphics2D g2 = (Graphics2D) g;

        if(lineCount > 1) {
            double incrAngle = (2*Math.PI) / lineCount;
            Line2D line = new Line2D.Double(300, -100, 300, 300);

            //draw i lines then rotate them an increasing amount each time
            for (int i = 1; i <= lineCount; i++) {
                AffineTransform rotatedLine = AffineTransform.getRotateInstance((i * incrAngle), centerX, centerY);
                g2.draw(((rotatedLine.createTransformedShape(line))));
            }
        }
    }
}

