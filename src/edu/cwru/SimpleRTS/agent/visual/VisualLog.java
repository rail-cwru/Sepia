package edu.cwru.SimpleRTS.agent.visual;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
/**
 * A text area to be used by agents.
 * It allows each agent to maintain an output separate from the others.
 * @author The Condor
 *
 */
public class VisualLog extends JFrame{

	private JTextArea textlog;
	private JScrollPane scrolling;
	private static final long serialVersionUID = 145642630197820949L;
	public VisualLog(String agentname, int windowwidth, int windowheight)
	{
		textlog = new JTextArea();
		textlog.setLineWrap(true);
		textlog.setEditable(false);
		scrolling = new JScrollPane(textlog);
//		this.add(scrolling);
		scrolling.setPreferredSize(new Dimension(windowwidth,windowheight));
		((DefaultCaret)textlog.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		this.getContentPane().add(scrolling);
		this.pack();
		this.setTitle(agentname+"'s Log");
		this.setVisible(true);
	}
	/**
	 * Clear the log, deleting all entries.
	 */
	public void clearLog()
	{
		textlog.setText("");
	}
	
	
	/**
	 * Add another line to the log
	 * @param newline A line to print
	 */
	public void writeLine(String newline)
	{
		//Scroll to the bottom only if you are at the bottom
		boolean shouldscroll = (scrolling.getVerticalScrollBar().getValue()+scrolling.getVerticalScrollBar().getModel().getExtent()==scrolling.getVerticalScrollBar().getMaximum());
		textlog.append(((!textlog.getText().equals(""))?"\n":"")+newline);
		if (shouldscroll)
		{
			
			SwingUtilities.invokeLater(new Runnable(){public void run(){
				scrolling.getVerticalScrollBar().setValue(scrolling.getVerticalScrollBar().getMaximum());
			}
			}
					);
		}
	}
	int t;
	@Override public void setPreferredSize(Dimension preferredSize)
	{
		scrolling.setPreferredSize(preferredSize);
		this.pack();
	}
}
