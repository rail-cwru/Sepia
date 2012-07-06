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
package edu.cwru.sepia.agent.visual;

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
