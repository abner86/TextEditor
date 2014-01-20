import com.jtattoo.plaf.texture.TextureLookAndFeel;

import javax.swing.*;

/**
 * Created by abner on 1/13/14.
 */
public class Main {

    public static void main(String[] args) {
        try {
            //JTattoo.jar
            UIManager.setLookAndFeel(new TextureLookAndFeel());
        } catch (Exception e) {

        }

        JFrame frame = new Editor(); //Create and show the GUI
        frame.setTitle("Editor"); // set the title of the window
        frame.setSize(800, 720); //set the initial size of the window
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
