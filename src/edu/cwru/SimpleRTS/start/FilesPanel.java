package edu.cwru.SimpleRTS.start;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.cwru.SimpleRTS.start.StartWindow;

@SuppressWarnings("serial")
public class FilesPanel extends JPanel {

    public FilesPanel() {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Files");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(StartWindow.TITLE_FONT));
        this.setBorder(border);

        JLabel configLabel = new JLabel("Config File:");
        JLabel mapLabel = new JLabel("Map File:");
        JTextField configField = new JTextField(20);
        JTextField mapField = new JTextField(20);
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
    }

}
