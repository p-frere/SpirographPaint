import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Handles the design and functionality of the Gallery UI
 * Displays up to 12 images
 */
public class Gallery extends JPanel {

    private ArrayList<JToggleButton> images = new ArrayList<>();    //Stores the images as Toggle Buttons
    private JPanel controls, display;                               //Sub sections of the gallery
    private UI ui;                                                  //Links the UI where the gallery is displayed

    public Gallery(UI ui){
        //creates the gallery tab
        this.setBorder(BorderFactory.createEmptyBorder(5,10,10,10));
        this.setPreferredSize(new Dimension(350, 700));
        this.setVisible(true);
        this.setBackground(Color.WHITE);
        this.setLayout(new FlowLayout());
        this.ui = ui;

        //sets up controls
        controls = new JPanel();
        controls.setPreferredSize(new Dimension(330, 33));
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(e -> deleteImage());
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> ui.save());
        controls.add(deleteBtn);
        controls.add(saveBtn);

        //display
        display = new JPanel();
        display.setPreferredSize(new Dimension(330, 650));

        this.add(controls);
        this.add(display);
    }

    /**
     * Adds Image to the gallery
     * @param image BufferedImage to diplay
     */
    public void addImage(BufferedImage image){
        //displays no more than 15 images
        if (images.size() < 15) {
            Image smallerImage = image.getScaledInstance(100, 100, 2);

            //Each image is made into a ToggleButton that can be selected
            //Behaviour:
            images.add(new JToggleButton(new ImageIcon(smallerImage)));
            JToggleButton currentButton = images.get(images.size() - 1);
            currentButton.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    currentButton.setBorder(new LineBorder(Color.GREEN, 5));
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    currentButton.setBorder(new LineBorder(Color.WHITE));
                }
            });
            //Defaults and appearance:
            currentButton.setSelected(false);
            currentButton.setBorder(new LineBorder(Color.WHITE));
            currentButton.setPreferredSize(new Dimension(100, 100));
            display.add(currentButton);
            revalidate();
            repaint();
        }
    }

    /** Deletes image from the gallery tab */
    private void deleteImage(){
        ArrayList<JToggleButton> toRemove = new ArrayList<>();
        //removes from canvas
        for(JToggleButton image : images){
            if (image.isSelected()){
                display.remove(image);
                toRemove.add(image);
            }
        }
        //removes from arraylist:
        images.removeAll(toRemove);
        revalidate();
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
