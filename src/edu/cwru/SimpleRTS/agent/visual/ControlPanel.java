package edu.cwru.SimpleRTS.agent.visual;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton stepButton;

    public ControlPanel() {
        stepButton = new JButton("step");
        stepButton.setSize(150, 100);
        add(stepButton);
    }

	public void addStepperListener(ActionListener l) {
		stepButton.addActionListener(l);
	}
	
	public void removeStepperListener(ActionListener l) {
		stepButton.removeActionListener(l);
	}
}
