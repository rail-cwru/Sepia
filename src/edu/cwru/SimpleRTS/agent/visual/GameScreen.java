package edu.cwru.SimpleRTS.agent.visual;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.ResourceView;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;

public class GameScreen extends JFrame implements KeyListener, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int SCALING_FACTOR = 32;
	
	private PaintPanel canvas;
	private StateView currentState;
	private int tlx;
	private int tly;
	
	public GameScreen(VisualAgent agent) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(800,600);
		setTitle("SimpleRTS");
		addKeyListener(this);
		
		canvas = this.new PaintPanel();
		JScrollPane scroll = new JScrollPane(canvas);
		add(scroll,BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void updateState(StateView state) {
		this.currentState = state;
		canvas.repaint();
	}
	
	public void close() {
		dispose();
	}
	
	public void addCanvasKeyListener(KeyListener l) {
		addKeyListener(l);
		canvas.addKeyListener(l);
	}
	
	public void removeCanvasKeyListener(KeyListener l) {
		removeKeyListener(l);
		canvas.removeKeyListener(l);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				if(tly > 0)
					tly--;
				break;
			case KeyEvent.VK_DOWN:
				if(tly + getHeight()/SCALING_FACTOR < currentState.getYExtent())
				tly++;
				break;
			case KeyEvent.VK_LEFT:
				if(tlx > 0)
					tlx--;
				break;
			case KeyEvent.VK_RIGHT:
				if(tlx + getWidth()/SCALING_FACTOR < currentState.getXExtent())
					tlx++;	
				break;
		}
		canvas.repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	public int scaleX(int x) {
		return (x-tlx)*SCALING_FACTOR;
	}
	public int scaleY(int y) {
		return (y-tly)*(SCALING_FACTOR);
	}
	public int unscaleX(int x) {
		return x/SCALING_FACTOR+tlx;
	}
	public int unscaleY(int y) {
		return y/SCALING_FACTOR+tly;
	}
	@Override
	public void addMouseListener(MouseListener listener) {
		super.addMouseListener(listener);
		canvas.addMouseListener(listener);
	}
	private class PaintPanel extends JPanel implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PaintPanel() {
			setSize(800,600);
			this.addKeyListener(GameScreen.this);
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(new Color(0x99,0x66,0x33));//medium green
			g.fillRect(0, 0, getWidth(), getHeight());//background
			
			if(currentState == null)
				return;
			
			//draw trees
			DrawingStrategy tree = DrawingStrategy.treeGraphic();
			for(int id : currentState.getResourceNodeIds(ResourceNode.Type.TREE))
			{
				ResourceView node = currentState.getResourceNode(id);
				int x = scaleX(node.getXPosition());
				int y = scaleY(node.getYPosition());
				if(x < 0 || y < 0)
					continue;
				tree.draw(g, x, y);
			}
			DrawingStrategy mine = DrawingStrategy.mineGraphic();
			for(int id : currentState.getResourceNodeIds(ResourceNode.Type.GOLD_MINE))
			{
				ResourceView node = currentState.getResourceNode(id);
				int x = scaleX(node.getXPosition());
				int y = scaleY(node.getYPosition());
				if(x < 0 || y < 0)
					continue;
				mine.draw(g, x, y);
			}
			for(int id : currentState.getAllUnitIds())
			{
				UnitView unit = currentState.getUnit(id);
				int x = scaleX(unit.getXPosition());
				int y = scaleY(unit.getYPosition());
				if(x < 0 || y < 0 || x > getWidth() || y > getHeight())
					continue;
				DrawingStrategy letter = DrawingStrategy.charGraphic(unit.getTemplateView().getCharacter());
				g.setColor(playerColors[unit.getPlayer()]);
				letter.draw(g, x, y);
			}
			g.setColor(new Color(255,128,127));
			g.drawString(tlx+","+tly, getWidth()-32, getHeight()-1);
		}
	}
	public static final Color[] playerColors = new Color[] { new Color(255,0,0), new Color(0,255,0), new Color(0,0,255),
			new Color(255,255,0), new Color(255,0,255), new Color(0,255,255), new Color(255,255,255), new Color(0,0,0)};

}
