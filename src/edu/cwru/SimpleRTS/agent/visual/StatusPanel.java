package edu.cwru.SimpleRTS.agent.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class StatusPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextArea statusJTextArea;
	private JScrollPane jScrollPane;
	
	private String old;
	private StringBuffer textSB;
	
	public StatusPanel() {
		super(new GridBagLayout());
		TitledBorder border = new TitledBorder("Status");
		border.setBorder(new LineBorder(Color.BLACK, 2));
//		border.setTitleFont(border.getTitleFont().deriveFont(20f));
		this.setBorder(border);
		
		//setLayout(new BorderLayout());
		statusJTextArea = new JTextArea("Hello!\n");
		statusJTextArea.setLineWrap(true);
		statusJTextArea.setEditable(false);
		statusJTextArea.setCaretPosition(statusJTextArea.getDocument().getLength());
		jScrollPane = new JScrollPane(statusJTextArea);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(jScrollPane, gbc);
		
		textSB = new StringBuffer();
	}
	
	public void paintComponent(Graphics g) {
		statusJTextArea.setText(textSB.toString().trim());
		super.paintComponent(g);
	}
	
	public void append(String text) {
		text = text.trim();
		old = textSB.toString();
		textSB.append(text + "\n");
		if(!textSB.toString().trim().equals(old)) {
			this.repaint();
		}
	}
	
	public void clear() {
		textSB = new StringBuffer();
		old = textSB.toString();
		if(!textSB.toString().trim().equals(old)) {
			this.repaint();
		}
	}
}
