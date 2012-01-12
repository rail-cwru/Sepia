package edu.cwru.SimpleRTS.start;

import java.util.List;
import java.util.LinkedList;

import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import edu.cwru.SimpleRTS.start.FilesPanel;
import edu.cwru.SimpleRTS.start.AgentPanel;
import edu.cwru.SimpleRTS.start.CommandPanel;
import edu.cwru.SimpleRTS.start.CommandChangeListener;

@SuppressWarnings("serial")
public class StartWindow extends JFrame
    implements CommandChangeListener, ActionListener {

    static final float TITLE_FONT = 20f;
    static final Dimension AGENT_TABLE_SIZE = new Dimension(700, 200);

    private List<StartListener> listeners = new LinkedList<StartListener>();

    FilesPanel filesPanel;
    AgentPanel agentPanel;
    CommandPanel commandPanel;

    public StartWindow() {
        super("SimpleStart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel startPanel = new JPanel(new GridBagLayout());
        filesPanel = new FilesPanel(this);
        agentPanel = new AgentPanel();
        commandPanel = new CommandPanel(this, this);

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

        filesPanel.addCommandChangeListener(this);
        agentPanel.addCommandChangeListener(this);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                commandChanged();
            }
        });
    }

    public List<String> toArgList() {
        List<String> args = new LinkedList<String>();
        args.addAll(filesPanel.toArgList());
        args.addAll(agentPanel.toArgList());
        return args;
    }

    @Override
    public void commandChanged() {
        commandPanel.setArgs(toArgList());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String[] args = toArgList().toArray(new String[0]);
        for (StartListener startListener : listeners) {
            startListener.start(args);
        }
    }

    public void addStartListener(StartListener listener) {
        listeners.add(listener);
    }

    public void removeStartListener(StartListener listener) {
        listeners.remove(listener);
    }

}
