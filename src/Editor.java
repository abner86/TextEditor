import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
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
@SuppressWarnings("UndesirableClassUsage")
class Editor extends JFrame implements ActionListener {

    protected JTextArea textArea = new JTextArea();
    protected UndoManager undoManager = new UndoManager(); //for undo and redo function

    private JMenuBar menuBar = new JMenuBar();
    private JMenu file = new JMenu("File");
    private JMenu edit = new JMenu("Edit");

    private JMenuItem newFile = new JMenuItem("New");
    private JMenuItem openFile = new JMenuItem("Open");
    private JMenuItem saveFile = new JMenuItem("Save");
    private JMenuItem print = new JMenuItem("Print");
    private JMenuItem close = new JMenuItem("Exit");

    //Edit Menu
    private JMenuItem undo = new JMenuItem("Undo");
    private JMenuItem redo = new JMenuItem("Redo");
    private JMenuItem copy = new JMenuItem();
    private JMenuItem cut = new JMenuItem();

    JFileChooser dialog = new JFileChooser();

    //Buttons with Icons for toolbar
    private JToolBar toolbar = new JToolBar();
    private JButton undoButton = new JButton(new ImageIcon("src/Icons/Undo.png"));
    private JButton redoButton = new JButton(new ImageIcon("src/Icons/Redo.png"));
    private JButton newButton = new JButton(new ImageIcon("src/Icons/New_document.png"));
    private JButton saveButton = new JButton(new ImageIcon("src/Icons/Save.png"));
    private JButton openButton = new JButton(new ImageIcon("src/Icons/openFile.png"));
    private JButton printButton = new JButton(new ImageIcon("src/Icons/printer.png"));
    private JButton cutButton = new JButton(new ImageIcon("src/Icons/Cut.png"));
    private JButton copyButton = new JButton(new ImageIcon("src/Icons/Copy.png"));
    private JButton pasteButton = new JButton(new ImageIcon("src/Icons/Paste.png"));

    public Editor() {
        menuBar = setUpMenu();
        toolbar = makeToolbar();
    }

    private JMenuBar setUpMenu() {
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(textArea);

        //for undo/redo
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
                update();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sp);

        //enabling text drag in JTextArea
        textArea.setDragEnabled(true);

        setJMenuBar(menuBar);
        menuBar.add(this.file);
        menuBar.add(this.edit);

        //File Menu
        newFile.addActionListener(this);
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        file.add(this.newFile);

        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        file.add(this.openFile);

        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        file.add(this.saveFile);

        file.addSeparator();

        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        file.add(this.print);

        file.addSeparator();

        close.addActionListener(this);
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        file.add(this.close);

        //Edit Menu
        undo.setEnabled(false);
        undo.addActionListener(this);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        edit.add(this.undo);

        redo.setEnabled(false);
        redo.addActionListener(this);
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
        edit.add(this.redo);

        edit.addSeparator();

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

        JMenuItem paste = new JMenuItem();
        paste.addActionListener(this);
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        edit.add(paste);

        return menuBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == close) {
            System.exit(0);
        } else if (e.getSource() == openFile) {
            openFile();
            //saveAs.setEnabled(true);
        } else if (e.getSource() == saveFile) {
            saveFile();
        }

        //creates a new file
        if (e.getSource() == newFile) {
            textArea.setText("");
        }

        //print textArea
        if (e.getSource() == print) {
            Print.printComponent(textArea);
        }
        //undo
        if (e.getSource() == undo) {
            try {
                undoManager.undo();
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
        }

        //redo
        if (e.getSource() == redo) {
            try {
                undoManager.redo();
            } catch (Exception e1) {
                System.err.println(e1.getMessage());
            }
        }
    }

    private JToolBar makeToolbar() {
        //Actions
        DefaultEditorKit.CutAction cutAction = new DefaultEditorKit.CutAction();
        DefaultEditorKit.CopyAction copyAction = new DefaultEditorKit.CopyAction();
        DefaultEditorKit.PasteAction pasteAction = new DefaultEditorKit.PasteAction();

        //Toolbar
        add(toolbar, BorderLayout.NORTH);

        toolbar.add(newButton);
        newButton.setToolTipText("New Document");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == newButton) {
                    textArea.setText(" ");
                }
            }
        });

        toolbar.add(openButton);
        openButton.setToolTipText("Open");
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == openButton) {
                    openFile();
                }
            }
        });

        toolbar.add(saveButton);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == saveButton) {
                    saveFile();
                }
            }
        });
        toolbar.addSeparator();

        toolbar.add(printButton);
        printButton.setToolTipText("Print");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Print.printComponent(textArea);
            }
        });
        toolbar.addSeparator();


        // redo and undo buttons
        undoButton.setEnabled(false);
        undoButton.setToolTipText("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.undo();
                } catch (CannotUndoException e2) {
                    System.err.println(e2.getMessage());
                }
                update();
            }
        });
        toolbar.add(undoButton);

        redoButton.setEnabled(false);
        redoButton.setToolTipText("Redo");
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    undoManager.redo();
                } catch (CannotRedoException e3) {
                    System.err.println(e3.getMessage());
                }
            }
        });
        toolbar.add(redoButton);

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

        return toolbar;
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

    //updating for undo/redo
    public void update() {
        this.undo.setEnabled(undoManager.canUndo());
        this.redo.setEnabled(undoManager.canRedo());

        undoButton.setEnabled(undoManager.canUndo());
        redoButton.setEnabled(undoManager.canRedo());
    }

    public static void createAndShowGUI() {
        JFrame frame = new Editor(); //Create and show the GUI
        frame.setTitle("Editor"); // set the title of the window
        frame.setSize(800, 720); //set the initial size of the window
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}