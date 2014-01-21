import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * Created by abner on 1/13/14.
 */
class Editor extends JFrame implements ActionListener {

    protected final JTextArea textArea = new JTextArea();

    private JMenuBar menuBar = new JMenuBar();
    private JMenu file = new JMenu("File");
    private JMenu edit = new JMenu("Edit");

    private JMenuItem newFile = new JMenuItem("New");
    private JMenuItem openFile = new JMenuItem("Open");
    private JMenuItem saveFile = new JMenuItem("Save");
    //protected JMenuItem saveAs = new JMenuItem("SaveAs...");
    private JMenuItem print = new JMenuItem("Print");
    private JMenuItem close = new JMenuItem("Exit");

    //Edit Menu
    private JMenuItem undo = new JMenuItem();
    private JMenuItem redo = new JMenuItem();
    private JMenuItem copy = new JMenuItem();
    private JMenuItem paste = new JMenuItem();
    private JMenuItem cut = new JMenuItem();

    private boolean changed = false;

    JFileChooser dialog = new JFileChooser();

    //Buttons for toolbar
    private JToolBar toolbar = new JToolBar();
    private JButton printButton = new JButton(new ImageIcon("src/Icons/printer.png"));
    private JButton cutButton = new JButton(new ImageIcon("src/Icons/Cut.png"));
    private JButton copyButton = new JButton(new ImageIcon("src/Icons/Copy.png"));
    private JButton pasteButton = new JButton(new ImageIcon("src/Icons/Paste.png"));

    public Editor() {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp = new JBScrollPane(textArea);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sp);

        setJMenuBar(menuBar);
        menuBar.add(this.file);
        menuBar.add(this.edit);

        //File Menu
        newFile.addActionListener(this);
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        file.add(newFile);

        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        file.add(this.openFile);

        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        file.add(this.saveFile);

//        saveAs.addActionListener(this);
//        saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
//        file.add(saveAs);

        file.addSeparator();

        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        file.add(this.print);

        file.addSeparator();

        close.addActionListener(this);
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        file.add(this.close);

        //Edit Menu
//TODO: fix and implement undo and redo action listener
        undo.addActionListener(this);
        undo.setText("Undo");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        edit.add(this.undo);

        redo.addActionListener(this);
        redo.setText("Redo");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        edit.add(this.redo);

        cut.addActionListener(this);
        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        edit.add(this.cut);

        copy.addActionListener(this);
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        edit.add(this.copy);

        paste.addActionListener(this);
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        edit.add(this.paste);

        makeToolbar();
    }

//    public void tabPane() {
//        JTabbedPane tabbedPane = new JTabbedPane();
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == close) {
            saveOld(); //currently is not asking to save work when exiting application
            System.exit(0);
        } else if (e.getSource() == openFile) {
            openFile();
            //saveAs.setEnabled(true);
        } else if (e.getSource() == saveFile) {
            saveFile();
        }

        //creates a new file
        if (e.getSource() == newFile) {
            textArea.setText(" ");
        }

        //print textArea
        if (e.getSource() == print) {
            Print.printComponent(textArea);
        }

    }

    public JToolBar makeToolbar() {
        //TODO: fix the toolbar actionListener. Currently is not doing anything if copy/paste/cut button is press

        //Actions
        DefaultEditorKit.CutAction cutAction = new DefaultEditorKit.CutAction();
        DefaultEditorKit.CopyAction copyAction = new DefaultEditorKit.CopyAction();
        DefaultEditorKit.PasteAction pasteAction = new DefaultEditorKit.PasteAction();

        //Toolbar
        add(toolbar, BorderLayout.NORTH);

        toolbar.add(printButton);
        printButton.setToolTipText("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Print.printComponent(textArea);
            }
        });
        toolbar.addSeparator();

        toolbar.add(cutButton);
        cutButton.setToolTipText("Cut");
        cutButton.addActionListener(cutAction);

        toolbar.add(copyButton);
        copyButton.setToolTipText("Copy");
        copyButton.addActionListener(copyAction);

        pasteButton.setToolTipText("Paste");
        pasteButton.addActionListener(pasteAction);
        toolbar.add(pasteButton);

        toolbar.addSeparator();

        return toolbar;
    }

//    private void saveAs() {
//        if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//            saveFile();
//        }
//    }

    //an option to save current work when exiting application
    private void saveOld() {
        if (changed) {
            if (JOptionPane.showConfirmDialog(this, "Would you like to save " + dialog.getSelectedFile().getAbsolutePath()
                    + "?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveFile();
            }
        }
    }

    private void openFile() {
        int option = dialog.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            this.textArea.setText(" ");
            try {
                Scanner scan = new Scanner(new FileReader(dialog.getSelectedFile().getPath()));
                while (scan.hasNext()) {
                    this.textArea.append(scan.nextLine() + "\n");
                }
                setTitle(dialog.getSelectedFile().getName() + " - " + getTitle());
            } catch (Exception ex) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Editor can't find the file " + dialog.getSelectedFile().getName());
            }
        }
    }

    private void saveFile() {
        int option = dialog.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(dialog.getSelectedFile()));
                out.write(this.textArea.getText());
                out.close();
                setTitle(dialog.getSelectedFile().getName() + " - " + getTitle());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void createAndShowGUI() {
        JFrame frame = new Editor(); //Create and show the GUI
        frame.setTitle("Editor"); // set the title of the window
        frame.setSize(800, 720); //set the initial size of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
