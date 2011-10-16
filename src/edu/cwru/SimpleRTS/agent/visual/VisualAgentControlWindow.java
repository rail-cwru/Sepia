package edu.cwru.SimpleRTS.agent.visual;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class VisualAgentControlWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton stepper;
	
	public VisualAgentControlWindow() {
		stepper = new JButton("step");
		add(stepper);
		
		setVisible(true);
	}
	
	public void addStepperListener(ActionListener l) {
		stepper.addActionListener(l);
	}
	
	public void removeStepperListener(ActionListener l) {
		stepper.removeActionListener(l);
	}
}
