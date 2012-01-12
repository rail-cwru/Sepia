package edu.cwru.SimpleRTS.start;

import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import edu.cwru.SimpleRTS.start.FilesPanel;
import edu.cwru.SimpleRTS.start.AgentPanel;
import edu.cwru.SimpleRTS.start.CommandPanel;

@SuppressWarnings("serial")
public class StartWindow extends JFrame {

    static final float TITLE_FONT = 20f;

    FilesPanel filesPanel = new FilesPanel();
    AgentPanel agentPanel = new AgentPanel();
    CommandPanel commandPanel = new CommandPanel();

    public StartWindow() {
        super("SimpleStart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel startPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        startPanel.add(filesPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        startPanel.add(agentPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.1;
        startPanel.add(commandPanel, gbc);

        this.getContentPane().add(startPanel, BorderLayout.CENTER);
    }

}
