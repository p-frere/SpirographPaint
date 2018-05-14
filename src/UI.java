import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class UI extends JFrame{
    private Gallery gallery;
    private Canvas canvas;

    public void init() {
        //create main frame
        JFrame frame = new JFrame("Digital Doilies");
        JPanel pane = new JPanel();
        frame.setContentPane(pane);
        pane.setLayout(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        //add components & behaviour
        gallery = new Gallery(this);
        canvas = new Canvas(this);
        SectorLines sectorLines = new SectorLines();

        //Labels
        JLabel sectorsLabel = new JLabel("Number of Sectors");

        //Buttons
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(e -> canvas.clear());
        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> canvas.undo());
        JButton redoBtn = new JButton("Redo");
        redoBtn.addActionListener(e ->  canvas.redo());

        JToggleButton reflectPointsBtn = new JToggleButton("Reflect Points");
        reflectPointsBtn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                canvas.setReflected(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                canvas.setReflected(false);
            }
        });

        JToggleButton toggleLinesBtn = new JToggleButton("Toggle lines");
        toggleLinesBtn.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    sectorLines.setVisible(false);
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    sectorLines.setVisible(true);
                }
        });

        //Sliders
        JSlider brushSize = new JSlider(JSlider.HORIZONTAL, 1, 50, 9);
        //brushSize.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        brushSize.addChangeListener(e -> canvas.setBrushSize(brushSize.getValue()));
        brushSize.setMajorTickSpacing(10);
        brushSize.setPaintTicks(true);
        Hashtable<Integer, JLabel> brushSizeLabels = new Hashtable<>();
        brushSizeLabels.put( 1, new JLabel("Small Brush") );
        brushSizeLabels.put( 50, new JLabel("Big Brush") );
        brushSize.setLabelTable( brushSizeLabels );
        brushSize.setPaintLabels(true);

        Integer lineMax = 20;
        JSlider sectorCount = new JSlider(JSlider.HORIZONTAL, 1, lineMax, 1);
        sectorCount.addChangeListener(e -> { canvas.setLineCount(sectorCount.getValue());
                                                sectorLines.setLineCount(sectorCount.getValue());});
        sectorCount.setMajorTickSpacing(1);
        sectorCount.setPaintTicks(true);
        Hashtable<Integer, JLabel>  sectorLabels = new Hashtable<>();
        for(int i = 1; i <= lineMax/2; i++){
            sectorLabels.put( i*2, new JLabel(Integer.toString(i*2)) );
        }
        sectorCount.setLabelTable( sectorLabels );
        sectorCount.setPaintLabels(true);

        //Colour Palette
        AbstractColorChooserPanel colorPalette = new JColorChooser().getChooserPanels()[0];
        colorPalette.getColorSelectionModel().addChangeListener(e -> canvas.setBrushColor(colorPalette.getColorSelectionModel().getSelectedColor()));
        JPanel colorPalettePanel = (JPanel) colorPalette.getComponent(0);
        colorPalettePanel.remove(1);
        colorPalettePanel.remove(1);

        JToggleButton eraserBtn = new JToggleButton("Eraser");
        eraserBtn.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                canvas.eraser(true);
                colorPalette.setEnabled(false);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                canvas.eraser(false);
                colorPalette.setEnabled(true);
            }
        });

        //Jframes and structure
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout());
        toolbar.setPreferredSize(new Dimension(1000, 190));

        JPanel paintBox = new JPanel();
        paintBox.add(colorPalettePanel);

        JPanel sliders = new JPanel();
        sliders.setLayout(new GridLayout(4,1));
        sliders.add(sectorsLabel);
        sliders.add(sectorCount);
        sliders.add(brushSize);

        JPanel buttonGroup = new JPanel();
        buttonGroup.setLayout(new GridLayout(2,3));

        JLayeredPane layeredPane = getLayeredPane();
        sectorLines.setLocation(0, 0);
        layeredPane.add(canvas, Integer.valueOf(1));
        layeredPane.add(sectorLines, Integer.valueOf(2));

        //add to panels
        buttonGroup.add(toggleLinesBtn);
        buttonGroup.add(reflectPointsBtn);
        buttonGroup.add(clearBtn);
        buttonGroup.add(undoBtn);
        buttonGroup.add(redoBtn);
        buttonGroup.add(eraserBtn);

        toolbar.add(buttonGroup);
        toolbar.add(paintBox);
        toolbar.add(sliders);

        pane.add(layeredPane, BorderLayout.CENTER);
        pane.add(toolbar, BorderLayout.SOUTH);
        pane.add(gallery, BorderLayout.EAST);

        frame.setSize(1000, 850);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public void addImage(BufferedImage image){
        gallery.addImage(image);
    }
    public void save(){
        canvas.save();
    }
}
