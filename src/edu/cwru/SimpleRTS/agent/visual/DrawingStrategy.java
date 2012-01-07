package edu.cwru.SimpleRTS.agent.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public abstract class DrawingStrategy {

	protected Rectangle bounds;
	
	private static DrawingStrategy tree;
	private static DrawingStrategy mine;
	private static Map<Character,DrawingStrategy> letters;
	private static DrawingStrategy selected;
	
	
	public abstract void draw(Graphics g, int tlx, int tly);
	
	public static DrawingStrategy treeGraphic() {
		if(tree == null)
		{
			tree = new DrawingStrategy() {
				@Override
				public void draw(Graphics g, int tlx, int tly) {
					Color previous = g.getColor();
					g.setColor(new Color(0,127,0));
					Polygon top = new Polygon();
					top.addPoint(tlx+16, tly+2);
					top.addPoint(tlx+4, tly+12);
					top.addPoint(tlx+8, tly+12);
					top.addPoint(tlx+2, tly+22);
					top.addPoint(tlx+30, tly+22);
					top.addPoint(tlx+24, tly+12);
					top.addPoint(tlx+28, tly+12);
					g.fillPolygon(top);
					g.setColor(new Color(0xA5,0x2A,0x2A));
					g.fillRect(tlx+10, tly+22, 12, 14);
					g.setColor(previous);
				}
			};
		}
		return tree;
	}
	
	public static DrawingStrategy mineGraphic() {
		if(mine == null)
		{
			mine = new DrawingStrategy() {
				@Override
				public void draw(Graphics g, int tlx, int tly) {
					Color previous = g.getColor();
					g.setColor(new Color(0xFF,0xFF,0x33));					
					g.fillRect(tlx, tly, 32, 32);
					g.setColor(previous);
				}
			};
		}
		return mine;
	}
	
	public static DrawingStrategy charGraphic(char c) {
		if(letters == null)
			letters = new HashMap<Character,DrawingStrategy>();
		DrawingStrategy strategy = letters.get(c);
		if(strategy == null)
		{
			strategy = new DrawingStrategy() {
				private char c;
				public DrawingStrategy setChar(char c) {
					this.c = c;
					return this;
				}
				@Override
				public void draw(Graphics g, int tlx, int tly) {
					Font old = g.getFont();
					g.setFont(new Font(old.getFamily(),old.getStyle(),24));
					g.drawChars(new char[]{c}, 0, 1, tlx+8, tly+24);
					g.setFont(old);
				}				
			}.setChar(c);
			letters.put(c, strategy);
		}
		return strategy;
	}
	
	public static DrawingStrategy selectedGraphic() {
		if(selected == null)
		{
			selected = new DrawingStrategy() {
				@Override
				public void draw(Graphics g, int tlx, int tly) {
					Color previous = g.getColor();
					g.setColor(new Color(0xFF,0xFF,0xFF));	
					g.drawRect(tlx, tly, 32, 32);
					g.setColor(previous);
				}
			};
		}
		return selected;
	}
}







