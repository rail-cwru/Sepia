/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.start;

import java.util.List;
import java.util.LinkedList;

import java.io.File;
import java.net.URI;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

import edu.cwru.sepia.start.StartWindow;

@SuppressWarnings("serial")
public class FilesPanel extends JPanel implements DocumentListener {

    private List<CommandChangeListener> commandChangeListeners =
        new LinkedList<CommandChangeListener>();

    JTextField configField;
    JTextField mapField;

    public FilesPanel(final Component parent) {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Files");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(StartWindow.TITLE_FONT));
        this.setBorder(border);

        JLabel configLabel = new JLabel("Config File:");
        JLabel mapLabel = new JLabel("Map File:");
        configField = new JTextField(20);
        mapField = new JTextField(20);
        JButton configButton = new JButton("Browse");
        JButton mapButton = new JButton("Browse");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        add(configLabel, gbc);

        gbc.gridy = 1;
        add(mapLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        add(configField, gbc);

        gbc.gridy = 1;
        add(mapField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        add(configButton, gbc);

        gbc.gridy = 1;
        add(mapButton, gbc);

        configField.getDocument().addDocumentListener(this);
        mapField.getDocument().addDocumentListener(this);

        configButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = getFileName(parent,
                    "Config Files", "xml");
                if (selectedFile != null) {
                    configField.setText(selectedFile);
                }
            }
        });

        mapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = getFileName(parent,
                    "Map Files", "xml", "map");
                if (selectedFile != null) {
                    mapField.setText(selectedFile);
                }
            }
        });
    }

    private String getFileName(Component parent,
        String filterDescription, String... extensions) {
        JFileChooser fc = new JFileChooser(new File("."));
        FileNameExtensionFilter filter =
            new FileNameExtensionFilter(filterDescription, extensions);
        fc.setFileFilter(filter);
        int choice = fc.showOpenDialog(parent);
        if (choice == JFileChooser.APPROVE_OPTION) {
            URI chosenFile = fc.getSelectedFile().toURI();
            URI currentDirectory = new File(".").toURI();
            return currentDirectory.relativize(chosenFile).getPath();
        }
        return null;
    }

    public List<String> toArgList() {
        List<String> args = new LinkedList<String>();
        String configString = configField.getText().trim();
        if (configString.length() > 0) {
            args.add("--config");
            args.add(configString);
        }
        args.add(mapField.getText().trim());
        return args;
    }

    public void addCommandChangeListener(CommandChangeListener listener) {
        commandChangeListeners.add(listener);
    }

    public void removeCommandChangeListener(CommandChangeListener listener) {
        commandChangeListeners.remove(listener);
    }

    private void fireListeners() {
        for (CommandChangeListener listener : commandChangeListeners) {
            listener.commandChanged();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        fireListeners();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        fireListeners();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        fireListeners();
    }

}
