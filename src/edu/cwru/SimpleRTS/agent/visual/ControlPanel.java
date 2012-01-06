package edu.cwru.SimpleRTS.agent.visual;

import java.util.List;
import java.util.LinkedList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

    public static final int WIDTH = 250, HEIGHT = 50;

    private List<ActionListener> listeners = new LinkedList<ActionListener>();
    private Timer timer = new Timer(500, this);
	private JButton stepButton;
    private JToggleButton playButton;
    private boolean playing = false;

    public ControlPanel() {
        stepButton = new JButton("Step");
        playButton = new JToggleButton("Play");

        stepButton.addActionListener(this);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePlay();
            }
        });

        JPanel buttonPanel = new JPanel();
        Dimension buttonSize = new Dimension(WIDTH, HEIGHT);
        buttonPanel.setMaximumSize(buttonSize);
        buttonPanel.setMinimumSize(buttonSize);
        buttonPanel.setPreferredSize(buttonSize);
		buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(stepButton, BorderLayout.EAST);
        buttonPanel.add(playButton, BorderLayout.CENTER);

		setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
    }

    private void togglePlay() {
        if (playing) {
            playButton.setText("Play");
            stepButton.setEnabled(true);
            timer.stop();
        } else {
            playButton.setText("Pause");
            stepButton.setEnabled(false);
            timer.start();
        }
        playing = !playing;
        playButton.invalidate();
    }

	public void addStepperListener(ActionListener l) {
        listeners.add(l);
	}
	
	public void removeStepperListener(ActionListener l) {
        listeners.remove(l);
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        for (ActionListener listener : listeners) {
            listener.actionPerformed(e);
        }
    }
}
