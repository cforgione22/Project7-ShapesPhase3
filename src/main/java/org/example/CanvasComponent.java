package org.example;

import javax.management.remote.JMXConnectorServerFactory;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

public class CanvasComponent extends JComponent implements Serializable {


    private enum ShapeType // Provide an enumerated value for each shape considered.
            // distinct values created in constant code convention form
    {
        LINE, BOX, OVAL
    }

    private ShapeType currentShape;  //used as a default shape
    private Color currentColor;     //used as a default color
    private List<Shape> shapes;     //collection shapes to be drawn
    private Point startPoint;//default starting point
    private Point endPoint;
    private boolean drawTrails;     // toggled value based on user response

    JFileChooser fileChooser = new JFileChooser();


    public CanvasComponent() // default constructor using the above default values.
    {
        shapes = new ArrayList<>(); //storing only lists that are there. Making sure our lists only have shape objects (safe collection - only objects of particular type)
        startPoint = new Point(0, 0);
        endPoint = new  Point(0,0);
        currentShape = ShapeType.BOX;
        currentColor = Color.BLACK;
        drawTrails = false;

    }

    /**
     * This overridden method traverses (going across) the list of shapes to make sure that all shapes are drawn
     */
    @Override
    public void paintComponent(Graphics g) {
        for (Shape s : shapes) {  //for each loop
            s.draw(g);
        }
    }

    /**
     * Establish the appropriate starting point as given to us by the caller. then, the shape
     * can be added to the list of shapes, ultimately drawing the shapes and flushing the
     * drawing to the user
     * @param p
     */
    public void createShape(Point p) {
        startPoint = p; //reset
        addShape(p);
        repaint();
    }

    /**
     * This method constructs the appropriate shape for given starting and ending point.
     * Then, the shape is including in the list of shapes.
     * @param p
     */

    private void addShape(Point p) {
        if (currentShape == ShapeType.BOX) {
            shapes.add(new Box(startPoint, p, currentColor));
        }
        else if (currentShape == ShapeType.LINE) {
            shapes.add(new Line(startPoint, p, currentColor));
        }
        else if(currentShape == ShapeType.OVAL) {
            shapes.add(new Oval(startPoint, p, currentColor));
            }
        }

    /**
     * This method allows for trailing drawings to appear, potentially filling the shape, if the
     * user desires.
     *
     * @param p
     */

    public void drawShape(Point p) //come from event itself
    {
        if (drawTrails) {
            addShape(p);
        } else {
            shapes.get(shapes.size() - 1).setP2(p); //setting the end point for the most recently added shape.
        }

        repaint();
    }



    /**
     * When the user presses ‘r’, the program should display a JFileChooser dialog box using the
     * showOpenDialog method. Check the result to make sure that a file name was selected. Use
     * an ObjectInputStream to input the list of shapes. This should be accomplished without
     * writing a loop that iterates through the file. Repaint the form so that the drawing read from
     * the file is displayed.
     */
//    public char showOpenDialog(JFileChooser chooseFile) {
//        //ObjectInputStream to input the list of shapes.
//        //Repaint the form so that the drawing read from the file is displayed.
//        return restore;
//    }



    /**
     * This method considers user keystrokes to establish the drawing configuration (shape type, color, etc.)
     *@param key
     */
    public void processKeys(char key) {
        switch (Character.toLowerCase(key)) {
            case 'b':
                currentShape = ShapeType.BOX;
                break;
            case 'l':
                currentShape = ShapeType.LINE;
                break;
            case 'o':
                currentShape = ShapeType.OVAL;
                break;
            case 'e':
                shapes.clear();
                repaint();
                break;
            case 't':
                drawTrails = !drawTrails;
                break;
            case 'c':           //swing's color chooser is a convenient way to input the color.
                currentColor = JColorChooser.showDialog(null, "Choose a shape color", currentColor);
                break;
            case 's':
                if (fileChooser.showSaveDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(shapes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case 'r':
                 if (fileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                     File file = fileChooser.getSelectedFile();
                     try {
                         FileInputStream fis = new FileInputStream(file);
                         ObjectInputStream ois = new ObjectInputStream(fis);
                         shapes = (List<Shape>)ois.readObject();
                         System.out.println(Arrays.toString(shapes.toArray()));
                         repaint();
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
                break;
                default: // Consider error processing  with alerts (unexpected values, logic errors, etc.)
                break;
        }
    }
}
