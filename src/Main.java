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

        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Editor.createAndShowGUI();
            }
        });
    }
}
