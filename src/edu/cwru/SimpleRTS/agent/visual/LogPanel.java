package edu.cwru.SimpleRTS.agent.visual;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class LogPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextArea logJTextArea;
	private JScrollPane jScrollPane;
	
	public LogPanel() {
		super(new GridBagLayout());
		TitledBorder border = new TitledBorder("Log");
		border.setBorder(new LineBorder(Color.BLACK, 2));
		border.setTitleFont(border.getTitleFont().deriveFont(20f));
		this.setBorder(border);
		
		//setLayout(new BorderLayout());
		logJTextArea = new JTextArea("Hello!\n");
		logJTextArea.setLineWrap(true);
		logJTextArea.setEditable(false);
		logJTextArea.setCaretPosition(logJTextArea.getDocument().getLength());
		jScrollPane = new JScrollPane(logJTextArea);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(jScrollPane, gbc);
	}
	
	public void append(String text) {
		text = text.trim();
		logJTextArea.append(text + "\n");
	}
	
	public void clear() {
		logJTextArea.setText("");
	}
}
