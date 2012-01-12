package edu.cwru.SimpleRTS.agent.visual;

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

	public GameScreen(GamePanel canvas, ControlPanel controlPanel, LogPanel logPanel) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setTitle("SimpleRTS");
		
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
