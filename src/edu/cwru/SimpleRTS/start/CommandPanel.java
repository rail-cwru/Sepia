package edu.cwru.SimpleRTS.start;

import java.util.List;
import java.util.LinkedList;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.cwru.SimpleRTS.start.StartWindow;

@SuppressWarnings("serial")
public class CommandPanel extends JPanel {

    JTextArea commandArea;

    public CommandPanel(final Component parent, ActionListener runListener) {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Command");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(StartWindow.TITLE_FONT));
        this.setBorder(border);

        commandArea = new JTextArea(3, 20);
        JButton copyButton = new JButton("Copy to Clipboard");
        JButton saveButton = new JButton("Save");
        JButton runButton = new JButton("Run");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(commandArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        add(copyButton, gbc);

        gbc.gridx = 2;
        add(saveButton, gbc);

        gbc.gridx = 3;
        add(runButton, gbc);

        commandArea.setEditable(false);
        commandArea.setLineWrap(true);

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection selection = new StringSelection(commandArea.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int choice = fc.showSaveDialog(parent);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    saveCommand(fc.getSelectedFile());
                }
            }
        });

        runButton.addActionListener(runListener);
    }

    public void saveCommand(File file) {
        String toSave = commandArea.getText();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
            writer.println(toSave);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void setArgs(List<String> args) {
        List<String> preArgs = new LinkedList<String>();
        preArgs.add("java");
        // TODO: Classpath?
        preArgs.add("edu.cwru.SimpleRTS.Main");
        preArgs.addAll(args);
        String command = join(preArgs, " ");
        commandArea.setText(command);
    }

    private static String join(List<String> args, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(delimiter);
        }
        // Remove trailing delimiter
        int n = builder.length();
        if (args.size() > 0) {
            builder.delete(n - delimiter.length(), n);
        }
        return builder.toString();
    }

}
