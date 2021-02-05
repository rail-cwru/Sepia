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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameScreen extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;

    public GameScreen(GamePanel canvas) {
        this(canvas, null, null);
    }

	public GameScreen(GamePanel canvas, ControlPanel controlPanel, StatusPanel logPanel) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setTitle("Sepia");
		
		add(canvas, BorderLayout.CENTER);

        if(controlPanel != null && logPanel == null) {
            add(controlPanel, BorderLayout.EAST);
        }
        else if(controlPanel != null && logPanel != null) {
        	JPanel holder = new JPanel();
        	holder.setLayout(new GridLayout(2,1));
        	holder.add(controlPanel);
        	holder.add(logPanel);
        	add(holder, BorderLayout.EAST);
        }
        else if(logPanel != null)
        {
            add(logPanel, BorderLayout.EAST);
        }
		
		setVisible(true);
	}

}
