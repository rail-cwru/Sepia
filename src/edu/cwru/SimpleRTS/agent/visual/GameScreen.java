package edu.cwru.SimpleRTS.agent.visual;

import java.awt.BorderLayout;
import java.io.Serializable;

import javax.swing.JFrame;

public class GameScreen extends JFrame implements Serializable {

	private static final long serialVersionUID = 1L;

    private GamePanel canvas;

	public GameScreen(GamePanel canvas) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(800, 600);
		setTitle("SimpleRTS");
        addKeyListener(canvas);
		
		this.canvas = canvas;
		add(canvas, BorderLayout.CENTER);
		
		setVisible(true);
	}

}
