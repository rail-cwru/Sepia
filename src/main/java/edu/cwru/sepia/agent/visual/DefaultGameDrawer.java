/**
 * 	Strategy Engine for Programming Intelligent Agents (SEPIA)
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.history.RevealedResourceNodeLog;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Tile.TerrainType;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

/**
 * The basic game drawer, sticks with drawing units, resources, and signs of attacks.
 *
 */
public class DefaultGameDrawer implements GameDrawer {

	private StateView state;
	private HistoryView history;
	private Color[] playerColors;
	private Color backgroundColor;
	private Color groundColor;
	private Color waterColor = Color.BLUE;
	private Color shallowColor = Color.GREEN;
	private Color cliffColor = new Color(150, 75, 0);
	private Color unknownColor = Color.MAGENTA;
	private boolean drawAttacks;
	private boolean drawUnits;
	private boolean drawResourceNodes;
	private int topBarHeight;
	public DefaultGameDrawer() {
		this(20, new Color(0x00,0xFF,0xFF), new Color(0x99,0x66,0x33), 	new Color[] {
	        new Color(255,0,0), new Color(0,255,0),
	        new Color(0,0,255), new Color(255,255,0),
	        new Color(255,0,255), new Color(0,255,255),
	        new Color(255,255,255), new Color(0,0,0)
	    },
	    true, true, true);
	}
	public DefaultGameDrawer(int topBarHeight, Color backgroundColor, Color groundColor, Color[] playerColors, boolean drawAttacks, boolean drawUnits, boolean drawResourceNodes) {
		this.topBarHeight = topBarHeight;
		this.playerColors = playerColors;
		this.backgroundColor = backgroundColor;
		this.groundColor = groundColor;
		this.drawAttacks = drawAttacks;
		this.drawUnits = drawUnits;
		this.drawResourceNodes = drawResourceNodes;
	}
	
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#drawTile(java.awt.Graphics, int, int)
	 */
	@Override
	public void drawTile(DrawingContext context, Graphics g, int x, int y) {
        if(state == null)
            return;
        if(x < 0 || y < 0 || x > context.getPixelWidth() || y > context.getPixelHeight())
            return;
        int tlPixelX = context.convertGameWorldToPixelX(x);
        int tlPixelY = context.convertGameWorldToPixelY(y);
        int tilePixelXSize = context.getScalingFactor();
        int tilePixelYSize = context.getScalingFactor(); 
        switch (state.terrainAt(x,y)) {
        case LAND:
        	drawTerrainLand(g, tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
        	break;
        case WATER:
        	drawTerrainWater(g, tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
        	break;
        case SHALLOWS:
        	drawTerrainShallows(g, tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
        	break;
        case CLIFF:
        	drawTerrainCliff(g, tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
        	break;
        default:
        	drawTerrainOther(g, tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize, state.terrainAt(x, y));
        }
        Color oldColor = g.getColor();
		
        
        //Draw some tiny numbers
        Font oldFont = g.getFont();
        g.setFont(g.getFont().deriveFont(7f));
        g.setColor(new Color(255,128,60));
        for (int i = 0; i<state.getXExtent(); i++)
        	for (int j = 0; j<state.getYExtent(); j++)
        		g.drawString(i+","+j, context.convertGameWorldToPixelX(i)+1, context.convertGameWorldToPixelY(j)+7);
        g.setFont(oldFont);
        g.setColor(oldColor);
        //Draw the resource nodes
        if (drawResourceNodes) {
        	Integer resourceNodeId = state.resourceAt(x, y);
        	
        	if (resourceNodeId != null) {
        		ResourceView resourceNode = state.getResourceNode(resourceNodeId);
	        	drawResourceNode(context, g, x, y, resourceNode);
        	}
        }
        
        //draw units
        if (drawUnits) {
        	Integer unitId = state.unitAt(x, y);
        	if (unitId != null) {
		        UnitView unit = state.getUnit(unitId);
		        drawUnit(context, g, x, y, unit);
        	}
        }
        
        //draw fog of war
        DrawingStrategy fog = DrawingStrategy.fogGraphic();
		if (!state.canSee(x, y))
			fog.draw(g, context.convertGameWorldToPixelX(x), context.convertGameWorldToPixelY(y));
        
        if (history!=null)
        {
	        //draw revealed resources
	        DrawingStrategy revealedTree = DrawingStrategy.revealedTreeGraphic();
	        DrawingStrategy revealedMine = DrawingStrategy.revealedMineGraphic();
	        for (RevealedResourceNodeLog rrl : history.getRevealedResourceNodeLogs())
	        {
	        	if (rrl.getResourceNodeXPosition() == x && rrl.getResourceNodeYPosition() == y)
	        	if (rrl.getResourceNodeType()==Type.GOLD_MINE)
	        	{
	        		revealedMine.draw(g, context.convertGameWorldToPixelX(x), context.convertGameWorldToPixelY(y));
	        	}
	        	else if (rrl.getResourceNodeType()==Type.TREE)
	        	{
	        		revealedTree.draw(g, context.convertGameWorldToPixelX(x), context.convertGameWorldToPixelY(y));
	        	}
	        }
        }
        
	}

	/**
	 * @param g
	 * @param tlPixelX
	 * @param tlPixelY
	 * @param tilePixelXSize
	 * @param tilePixelYSize
	 */
	void drawTerrainLand(Graphics g, int tlPixelX, int tlPixelY,
			int tilePixelXSize, int tilePixelYSize) {
		Color oldColor = g.getColor();
		g.setColor(groundColor);
		g.fillRect(tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
		g.setColor(oldColor);
	}
	/**
	 * @param g
	 * @param tlPixelX
	 * @param tlPixelY
	 * @param tilePixelXSize
	 * @param tilePixelYSize
	 */
	void drawTerrainWater(Graphics g, int tlPixelX, int tlPixelY,
			int tilePixelXSize, int tilePixelYSize) {
		Color oldColor = g.getColor();
		g.setColor(waterColor);
		g.fillRect(tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
		g.setColor(oldColor);
	}
	/**
	 * @param g
	 * @param tlPixelX
	 * @param tlPixelY
	 * @param tilePixelXSize
	 * @param tilePixelYSize
	 */
	void drawTerrainShallows(Graphics g, int tlPixelX, int tlPixelY,
			int tilePixelXSize, int tilePixelYSize) {
		Color oldColor = g.getColor();
		g.setColor(shallowColor);
		g.fillRect(tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
		g.setColor(oldColor);
	}
	/**
	 * @param g
	 * @param tlPixelX
	 * @param tlPixelY
	 * @param tilePixelXSize
	 * @param tilePixelYSize
	 */
	void drawTerrainCliff(Graphics g, int tlPixelX, int tlPixelY,
			int tilePixelXSize, int tilePixelYSize) {
		Color oldColor = g.getColor();
		g.setColor(cliffColor);
		g.fillRect(tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
		g.setColor(oldColor);
		
	}
	/**
	 * Draw any terrain that is not one of the predefined ones
	 * @param g
	 * @param tlPixelX
	 * @param tlPixelY
	 * @param tilePixelXSize
	 * @param tilePixelYSize
	 * @param terrainAt
	 */
	void drawTerrainOther(Graphics g, int tlPixelX, int tlPixelY,
			int tilePixelXSize, int tilePixelYSize, TerrainType terrainType) {
		Color oldColor = g.getColor();
		g.setColor(unknownColor);
		g.fillRect(tlPixelX, tlPixelY, tilePixelXSize, tilePixelYSize);
		g.setColor(Color.black);
//		g.drawString(terrainType.toString(), tlPixelX + tilePixelXSize/2, tlPixelY + tilePixelYSize/2);
		g.setColor(oldColor);
		
	}
	/**
	 * Draw a resource node at a particular x and y coordinate
	 * @param context
	 * @param g
	 * @param x game world x coordinate
	 * @param y game world y coordinate
	 * @param resourceNode
	 */
	public void drawResourceNode(DrawingContext context, Graphics g, int x,
			int y, ResourceView resourceNode) {
		switch(resourceNode.getType()) {
        case TREE:
        	drawTree(g, context.convertGameWorldToPixelX(resourceNode.getXPosition()), context.convertGameWorldToPixelY(resourceNode.getYPosition()));
        	break;
        case GOLD_MINE:
        	drawMine(g, context.convertGameWorldToPixelX(resourceNode.getXPosition()), context.convertGameWorldToPixelY(resourceNode.getYPosition()));
        	break;
        default:
        	break;
        }
	}
	/**
	 * Draws a unit in a specific place on the map
	 * @param context
	 * @param g
	 * @param x game world x coordinate
	 * @param y game world y coordinate
	 * @param unit
	 */
	public void drawUnit(DrawingContext context, Graphics g, int x, int y,
			UnitView unit) {
            DrawingStrategy letter = DrawingStrategy.charGraphic(unit.getTemplateView().getCharacter());
            g.setColor(playerColors[unit.getTemplateView().getPlayer()]);
            letter.draw(g, context.convertGameWorldToPixelX(x), context.convertGameWorldToPixelY(y));
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#drawBackground(java.awt.Graphics)
	 */
	@Override
	public void drawBackground(DrawingContext context, Graphics g) {
		g.setColor(backgroundColor);//aqua for things out of bounds
        g.fillRect(0, 0, context.getPixelWidth(), context.getPixelHeight());//background

	}

	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#drawForeground(java.awt.Graphics)
	 */
	@Override
	public void drawForeground(DrawingContext context, Graphics g) {
		//Draw weapons fire
        if (drawAttacks && history!=null)
        {
	        Color lastcolor = g.getColor();
	        for (DamageLog damage :history.getDamageLogs(state.getTurnNumber()-1))
	        {
	        	UnitView attacker = state.getUnit(damage.getAttackerID());
	        	UnitView defender = state.getUnit(damage.getDefenderID());
	        	g.setColor(playerColors[damage.getAttackerController()]);
	        	if (attacker != null && defender != null)
	        	{
	        		//do an offset so you can see two things shooting at each other
	        		int yplayeroffset = damage.getAttackerController()*2;
	        		g.drawLine(context.convertGameWorldToPixelX(attacker.getXPosition())+context.getScalingFactor()/2, context.convertGameWorldToPixelY(attacker.getYPosition())+context.getScalingFactor()/2, context.convertGameWorldToPixelX(defender.getXPosition())+context.getScalingFactor()/2, context.convertGameWorldToPixelY(defender.getYPosition())+context.getScalingFactor()/2 + yplayeroffset);
	        		g.drawString(Integer.toString(damage.getDamage()), (int)(context.convertGameWorldToPixelX(attacker.getXPosition())*0.75+context.convertGameWorldToPixelX(defender.getXPosition())*0.25)+context.getScalingFactor()/2, (int)(context.convertGameWorldToPixelY(attacker.getYPosition())*.75+context.convertGameWorldToPixelY(defender.getYPosition())*.25)+context.getScalingFactor()/2+yplayeroffset);
	        	}
	        	else if (attacker!=null) 
	        	{
	        		//Just draw the number over the attacker
	        		g.drawString(Integer.toString(damage.getDamage()), context.convertGameWorldToPixelX(attacker.getXPosition())+context.getScalingFactor()/2, context.convertGameWorldToPixelY(attacker.getYPosition())+context.getScalingFactor()/2);
	        	}
	        	else if (defender!=null)
	        	{
	        		//Just draw the number over the defender
	        		g.drawString(Integer.toString(damage.getDamage()), context.convertGameWorldToPixelX(defender.getXPosition())+context.getScalingFactor()/2, context.convertGameWorldToPixelY(defender.getYPosition())+context.getScalingFactor()/2);
	        	}
	        	
	        }
	        g.setColor(lastcolor);
        }
        
        g.setColor(new Color(255,128,127));
        g.drawString("TL:"+context.getGameWorldTopLeftX()+","+context.getGameWorldTopLeftY(), context.getPixelWidth()-50, context.getPixelHeight()-16);
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#updateState(edu.cwru.sepia.environment.model.state.State.StateView, edu.cwru.sepia.environment.model.history.History.HistoryView)
	 */
	@Override
	public void updateState(StateView state, HistoryView history) {
		this.state = state;
		this.history = history;
	}

	public void drawTerrainLand(Graphics g, int topLeftX, int topLeftY) {
		
	}
	
	/**
	 * Internal method to draw happy little trees.
	 * <br>Override to change what trees look like
	 */
	protected void drawTree(Graphics g, int topLeftX, int topLeftY) {
		Color previous = g.getColor();
		g.setColor(new Color(0,127,0));
		Polygon top = new Polygon();
		top.addPoint(topLeftX+16, topLeftY+2);
		top.addPoint(topLeftX+4, topLeftY+12);
		top.addPoint(topLeftX+8, topLeftY+12);
		top.addPoint(topLeftX+2, topLeftY+22);
		top.addPoint(topLeftX+30, topLeftY+22);
		top.addPoint(topLeftX+24, topLeftY+12);
		top.addPoint(topLeftX+28, topLeftY+12);
		g.fillPolygon(top);
		g.setColor(new Color(0xA5,0x2A,0x2A));
		g.fillRect(topLeftX+12, topLeftY+22, 8, 10);
		g.setColor(previous);
	}
	/**
	 * Internal method to draw a gold mine.
	 * <br>Override 
	 * @param g
	 * @param scaleX
	 * @param scaleY
	 */
	void drawMine(Graphics g, int topLeftX, int topLeftY) {
		Color previous = g.getColor();
		g.setColor(new Color(0xFF,0xFF,0x33));					
		g.fillRect(topLeftX+6, topLeftY+6, 20, 20);
		g.setColor(previous);
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#getTopBarHeight()
	 */
	@Override
	public int getTopBarHeight() {
		return topBarHeight;
	}
	/* (non-Javadoc)
	 * @see edu.cwru.sepia.agent.visual.GameDrawer#drawTopBar(edu.cwru.sepia.agent.visual.DrawingContext, java.awt.Graphics)
	 */
	@Override
	public void drawTopBar(DrawingContext context, Graphics g) {
		int barTlx = 0;
		int barTly = 0;
		g.setColor(Color.gray);
		g.fillRect(barTlx, barTly, context.getPixelWidth(), context.getTopBarHeight());
		g.setColor(Color.black);
		g.drawString("Gold: " + state.getResourceAmount(context.getPlayerNumber(), ResourceType.GOLD), barTlx+50, barTly+10);
		g.drawString("Wood: " + state.getResourceAmount(context.getPlayerNumber(), ResourceType.WOOD), barTlx+125, barTly+10);
		g.drawString("Food: " + state.getSupplyAmount(context.getPlayerNumber())+"/"+state.getSupplyCap(context.getPlayerNumber()), barTlx+200, barTly+10);
	}
}
