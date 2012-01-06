package edu.cwru.SimpleRTS.agent.visual;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JFrame;

public class GameScreen extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;

    public GameScreen(GamePanel canvas) {
        this(canvas, null);
    }

	public GameScreen(GamePanel canvas, ControlPanel controlPanel) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(800 + ControlPanel.WIDTH, 600);
		setTitle("SimpleRTS");
		
		add(canvas, BorderLayout.CENTER);

        if(controlPanel != null) {
            add(controlPanel, BorderLayout.EAST);
        }
		
		setVisible(true);
	}

}
