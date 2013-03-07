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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;  
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.JComponent;


import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.RevealedResourceNodeLog;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 800, HEIGHT = 600;
	public static final int SCALING_FACTOR = 32;
	public static final Color[] playerColors = new Color[] {
        new Color(255,0,0), new Color(0,255,0),
        new Color(0,0,255), new Color(255,255,0),
        new Color(255,0,255), new Color(0,255,255),
        new Color(255,255,255), new Color(0,0,0)
    };

	private StateView currentState;
	private HistoryView latestHistory;
	private int tlx;
	private int tly;

	private VisualAgent agent;
	private int playernum;
	private int selectedID;  	// left clicked
	private int infoVisSelectedID; // double clicked
	private Info info;
	
    public GamePanel(VisualAgent agent) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Add Key Bindings
        InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(KeyStroke.getKeyStroke("UP"), "shiftUp");
        map.put(KeyStroke.getKeyStroke("DOWN"), "shiftDown");
        map.put(KeyStroke.getKeyStroke("LEFT"), "shiftLeft");
        map.put(KeyStroke.getKeyStroke("RIGHT"), "shiftRight");

        ActionMap amap = getActionMap();
        amap.put("shiftUp", new ShiftAction(ShiftDirection.UP));
        amap.put("shiftDown", new ShiftAction(ShiftDirection.DOWN));
        amap.put("shiftLeft", new ShiftAction(ShiftDirection.LEFT));
        amap.put("shiftRight", new ShiftAction(ShiftDirection.RIGHT));
        
        this.addMouseListener(this.new GamePanelMouseListener());
        this.agent = agent;
        if(agent!=null)
        	this.playernum = agent.getPlayerNumber();
        selectedID = -1;
        infoVisSelectedID = -1;
    }

    public void reset() {
    	selectedID = -1;
        infoVisSelectedID = -1;
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	g.setColor(new Color(0x00,0xFF,0xFF));//aqua for things out of bounds
        g.fillRect(0, 0, getWidth(), getHeight());//background
        if(currentState == null)
            return;
        g.setColor(new Color(0x99,0x66,0x33));//brown color
        g.fillRect(scaleX(0), scaleY(0), scaleX(currentState.getXExtent())-scaleX(0),scaleY(currentState.getYExtent())-scaleY(0));//background
        Color oldcolor = g.getColor();
        
        //Draw some lines
        g.setColor(new Color(0x66,0x44,0x22));
        for (int i = 0; i<=currentState.getXExtent(); i++)
            g.drawLine(scaleX(i), scaleY(0), scaleX(i), scaleY(currentState.getYExtent()));
        for (int j = 0; j<=currentState.getYExtent(); j++)
            g.drawLine(scaleX(0), scaleY(j), scaleX(currentState.getXExtent()), scaleY(j));
        	
        //Draw some tiny numbers
        Font oldfont = g.getFont();
        g.setFont(g.getFont().deriveFont(7f));
        g.setColor(new Color(255,128,60));
        for (int i = 0; i<currentState.getXExtent(); i++)
        	for (int j = 0; j<currentState.getYExtent(); j++)
        		g.drawString(i+","+j, scaleX(i)+1, scaleY(j)+7);
        g.setFont(oldfont);
        g.setColor(oldcolor);
        
        
        //draw selected by left click
        if(selectedID>=0) {
        	UnitView unit = currentState.getUnit(selectedID);
        	if (unit != null)
        	{
	        	int x = scaleX(unit.getXPosition());
	            int y = scaleY(unit.getYPosition());
	            if(x >= 0 && y >= 0) {
	            	DrawingStrategy selected = DrawingStrategy.selectedGraphic();
	            	selected.draw(g, x, y);
	            }
        	}
        }
        
        if (latestHistory!=null)
        {
        //draw revealed resources
        DrawingStrategy revealedTree = DrawingStrategy.revealedTreeGraphic();
        DrawingStrategy revealedMine = DrawingStrategy.revealedMineGraphic();
        for (RevealedResourceNodeLog rrl : latestHistory.getRevealedResourceNodeLogs())
        {
        	int x = scaleX(rrl.getResourceNodeXPosition());
        	int y = scaleY(rrl.getResourceNodeYPosition());
        	if (rrl.getResourceNodeType()==Type.GOLD_MINE)
        	{
        		revealedMine.draw(g, x, y);
        	}
        	else if (rrl.getResourceNodeType()==Type.TREE)
        	{
        		revealedTree.draw(g, x, y);
        	}
        }
        }
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
        
        //draw mines
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
        
        //draw units
        for(int id : currentState.getAllUnitIds())
        {
            UnitView unit = currentState.getUnit(id);
            int x = scaleX(unit.getXPosition());
            int y = scaleY(unit.getYPosition());
            if(x < 0 || y < 0 || x > getWidth() || y > getHeight())
                continue;
            DrawingStrategy letter = DrawingStrategy.charGraphic(unit.getTemplateView().getCharacter());
            g.setColor(playerColors[unit.getTemplateView().getPlayer()]);
            letter.draw(g, x, y);
        }
        //Draw weapons fire
        if (latestHistory!=null)
        {
        Color lastcolor = g.getColor();
        for (DamageLog damage :latestHistory.getDamageLogs(currentState.getTurnNumber()-1))
        {
        	UnitView attacker = currentState.getUnit(damage.getAttackerID());
        	UnitView defender = currentState.getUnit(damage.getDefenderID());
        	g.setColor(playerColors[damage.getAttackerController()]);
        	if (attacker != null && defender != null)
        	{
        		//do an offset so you can see two things shooting at each other
        		int yplayeroffset = damage.getAttackerController()*2;
        		g.drawLine(scaleX(attacker.getXPosition())+SCALING_FACTOR/2, scaleY(attacker.getYPosition())+SCALING_FACTOR/2, scaleX(defender.getXPosition())+SCALING_FACTOR/2, scaleY(defender.getYPosition())+SCALING_FACTOR/2 + yplayeroffset);
        		g.drawString(Integer.toString(damage.getDamage()), (int)(scaleX(attacker.getXPosition())*0.75+scaleX(defender.getXPosition())*0.25)+SCALING_FACTOR/2, (int)(scaleY(attacker.getYPosition())*.75+scaleY(defender.getYPosition())*.25)+SCALING_FACTOR/2+yplayeroffset);
        	}
        	else if (attacker!=null) 
        	{
        		//Just draw the number over the attacker
        		g.drawString(Integer.toString(damage.getDamage()), scaleX(attacker.getXPosition())+SCALING_FACTOR/2, scaleY(attacker.getYPosition())+SCALING_FACTOR/2);
        	}
        	else if (defender!=null)
        	{
        		//Just draw the number over the defender
        		g.drawString(Integer.toString(damage.getDamage()), scaleX(defender.getXPosition())+SCALING_FACTOR/2, scaleY(defender.getYPosition())+SCALING_FACTOR/2);
        	}
        	
        }
        g.setColor(lastcolor);
        }
       
        //draw fog of war
        DrawingStrategy fog = DrawingStrategy.fogGraphic();
        for (int i = 0; i<currentState.getXExtent(); i++)
        	for (int j = 0; j<currentState.getYExtent(); j++)
            {
        		if (!currentState.canSee(i, j))
        			fog.draw(g, scaleX(i), scaleY(j));
            }
        
       
        g.setColor(new Color(255,128,127));
        g.drawString("TL:"+tlx+","+tly, getWidth()-50, getHeight()-16);
        g.drawString(currentState.getXExtent()+"x"+currentState.getYExtent(), getWidth()-50, getHeight()-1);
        //draw info vis (by double click)
        if(infoVisSelectedID>=0 && info!=null) {
        	if(currentState.getUnit(infoVisSelectedID)!=null) {
        		if(info.getX()>=0 && info.getY()>=0) {
        			DrawingStrategy infoBox = DrawingStrategy.infoGraphic();
        			infoBox.setInfo(info.getInfo());
        			infoBox.draw(g, info.getX(), info.getY());
        		}
        	}
        }
    }

    public static enum ShiftDirection {
        UP, DOWN, LEFT, RIGHT;
    }

    public class ShiftAction extends AbstractAction {

		private static final long serialVersionUID = 8622421006574238465L;
		
		ShiftDirection shiftDirection;

        public ShiftAction(ShiftDirection shiftDirection) {
            this.shiftDirection = shiftDirection;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        	if(currentState==null)
        		return;
            switch(shiftDirection) {
			case UP:
				if(tly > 0)
					tly--;
				break;
			case DOWN:
				if(tly + getHeight()/SCALING_FACTOR < currentState.getYExtent())
				tly++;
				break;
			case LEFT:
				if(tlx > 0)
					tlx--;
				break;
			case RIGHT:
				if(tlx + getWidth()/SCALING_FACTOR < currentState.getXExtent())
					tlx++;	
				break;
            }
            GamePanel.this.repaint();
        }

    }

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
	
	public void updateState(StateView state, HistoryView history) {
		this.currentState = state;
		this.latestHistory = history;
        this.repaint();
	}
	
	private class GamePanelMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			//int x = unscaleX(e.getX());
			//int y = unscaleY(e.getY());
			//System.out.println(x+","+y);
			
			if(agent!=null && agent.humanControllable)
				humanControl(e);
			if(agent == null || agent.infoVis)
				infoVisual(e);
			repaint();
		}

	}
	
	private class PopupActionMenu extends JPopupMenu {
		private static final long serialVersionUID = 7823418576361323507L;

		public PopupActionMenu(StateView state, MouseEvent e, UnitView selectedUnit) {
			int x = unscaleX(e.getX());
			int y = unscaleY(e.getY());
			if(state.unitAt(x, y)!=null) { 
				/** right click on a unit */
				int rightSelected = state.unitAt(x, y);
				UnitView targetUnit = state.getUnit(rightSelected);
				if (targetUnit.getID() == selectedUnit.getID()) {
					//Units may build at their own position
					if(selectedUnit.getTemplateView().getProduces()!=null &&
							selectedUnit.getTemplateView().getProduces().size()>0 &&
							selectedUnit.getTemplateView().canBuild()) {
						List<Integer> productions = selectedUnit.getTemplateView().getProduces();
						for(int prodTempID : productions) {
							JMenuItem bItem = new JMenuItem("Build " + state.getTemplate(prodTempID).getName());
							bItem.addActionListener(new PopupActionListener(selectedID, prodTempID, ActionType.COMPOUNDBUILD, x, y));
							add(bItem);
						}
					}
				}
				
				if(selectedUnit.getTemplateView().canAttack() && targetUnit.getTemplateView().getPlayer()!=playernum) {
					// attack the target
					//Action action = new TargetedAction(selectedID, ActionType.COMPOUNDATTACK, rightSelected);
					//log("=> Action: " + action);
					//agent.addAction(action);
					JMenuItem attackItem = new JMenuItem("Attack");
					attackItem.addActionListener(new PopupActionListener(selectedID, rightSelected, ActionType.COMPOUNDATTACK));
					add(attackItem);
				} else if(selectedUnit.getCargoAmount()>0 && 
						((targetUnit.getTemplateView().canAcceptGold() && selectedUnit.getCargoType()==ResourceType.GOLD) ||
								(targetUnit.getTemplateView().canAcceptWood() && selectedUnit.getCargoType()==ResourceType.WOOD))) {
					// target is townhall or Barracks, and the peasant holds the gold or wood
					JMenuItem depositItem = new JMenuItem("Deposit " + selectedUnit.getCargoType());
					depositItem.addActionListener(new PopupActionListener(selectedID, rightSelected, ActionType.COMPOUNDDEPOSIT));
					add(depositItem);
				} 
			} else if(state.resourceAt(x, y)!=null) { // gather resource if doable
				/** right click on a resource */
				int rightSelected = state.resourceAt(x, y);
				if(selectedUnit.getTemplateView().canGather()) {
					JMenuItem gatherItem = new JMenuItem("Gather " + state.getResourceNode(rightSelected).getType());
					gatherItem.addActionListener(new PopupActionListener(selectedID, rightSelected, ActionType.COMPOUNDGATHER));
					add(gatherItem);
				}
			} else { 
				/** right click on a blank */
				if(selectedUnit.getTemplateView().getProduces()!=null &&
						selectedUnit.getTemplateView().getProduces().size()>0 &&
						selectedUnit.getTemplateView().canBuild()) {
					// build some unit 
					List<Integer> productions = selectedUnit.getTemplateView().getProduces();
					for(int prodTempID : productions) {
						JMenuItem bItem = new JMenuItem("Build " + state.getTemplate(prodTempID).getName());
						bItem.addActionListener(new PopupActionListener(selectedID, prodTempID, ActionType.COMPOUNDBUILD, x, y));
						add(bItem);
					}
				}
				if(selectedUnit.getTemplateView().canMove()) {
					// move
					JMenuItem moveItem = new JMenuItem("Move");
					moveItem.addActionListener(new PopupActionListener(selectedID, -1, ActionType.COMPOUNDMOVE, x, y));
					add(moveItem);
				}
			}
			
			// if can produce (not build)
			if(selectedUnit.getTemplateView().getProduces()!=null &&
					selectedUnit.getTemplateView().getProduces().size()>0 &&
					!selectedUnit.getTemplateView().canBuild()) {
				// produce some unit
				List<Integer> productions = selectedUnit.getTemplateView().getProduces();
				for(int prodTempID : productions) {
					JMenuItem bItem = new JMenuItem("Produce " + state.getTemplate(prodTempID).getName());
					bItem.addActionListener(new PopupActionListener(selectedID, prodTempID, ActionType.COMPOUNDPRODUCE));
					add(bItem);
				}
			} 
		}
		
		private class PopupActionListener implements ActionListener {
			private int source;
			private int target;
			private ActionType actionType;
			private int x;
			private int y;
			public PopupActionListener(int source, int target, ActionType actionType) {
				this.source = source;
				this.target = target;
				this.actionType = actionType;
			}
			public PopupActionListener(int source, int target, ActionType actionType, int x, int y) {
				this.source = source;
				this.target = target;
				this.actionType = actionType;
				this.x = x;
				this.y = y;
			}
			public void actionPerformed(ActionEvent event) {
				Action action = null;
				switch (actionType) {
				case COMPOUNDATTACK:
					action = Action.createCompoundAttack(source, target);
					break;
				case COMPOUNDDEPOSIT:
					action = Action.createCompoundDeposit(source, target);
					break;
				case COMPOUNDPRODUCE:
					action = Action.createCompoundProduction(source, target);
					break;
				case COMPOUNDBUILD:
					action = Action.createCompoundBuild(source, target, x, y);
					break;
				case COMPOUNDGATHER:
					action = Action.createCompoundGather(source, target);
					break;
				case COMPOUNDMOVE:
					action = Action.createCompoundMove(source, x, y);
					break;
				default:
					return;
				}
				agent.writeLineVisual("=> Action: " + action);
				agent.addAction(action);
			}
		}
		
	}
	
	private void infoVisual(MouseEvent e) {
		int x = unscaleX(e.getX());
		int y = unscaleY(e.getY());
		StateView state = currentState;
		if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) { // double click
			//System.out.println("double clicked!");
			infoVisSelectedID = -1;
			info = null;
			if(state.unitAt(x, y)!=null) {
				infoVisSelectedID = state.unitAt(x, y);
				info = new Info(state, infoVisSelectedID);
			} else if(state.resourceAt(x, y)!=null) {
				infoVisSelectedID = state.resourceAt(x, y);
				info = new Info(state, infoVisSelectedID);
			}
		}
	}
	
	private void humanControl(MouseEvent e) {
		int x = unscaleX(e.getX());
		int y = unscaleY(e.getY());
		StateView state = currentState;
		//System.out.println(x+","+y);
		if(e.getButton()==MouseEvent.BUTTON1) { // left click
			//System.out.println("Left clicked");
			selectedID = -1;
			if(state.unitAt(x, y)!=null) {
				int leftSelected = state.unitAt(x, y);
				if(state.getUnit(leftSelected).getTemplateView().getPlayer()==playernum)
					selectedID = leftSelected;
				return ;
			}
		} else if(e.getButton()==MouseEvent.BUTTON3) { // right click
			//System.out.println("Right clicked");
			if(selectedID>=0) {
				UnitView myUnit = state.getUnit(selectedID);
				if(myUnit==null) { // the selected unit is dead or you can't see it
					selectedID = -1;
					return ;
				}
				PopupActionMenu actionMenu = new PopupActionMenu(currentState, e, myUnit);
				actionMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	private class Info{
		StateView state;
		int id;
		String info;
		
		public Info(StateView state, int id) {
			this.state = state;
			this.id = id;
		}
		public int getX() {
			if(state.getUnit(id)!=null)
				return scaleX(state.getUnit(id).getXPosition());
			else if (state.getResourceNode(id)!=null)
				return scaleX(state.getResourceNode(id).getXPosition());
			else
				return -1;
		}
		public int getY() {
			if(state.getUnit(id)!=null)
				return scaleY(state.getUnit(id).getYPosition());
			else if (state.getResourceNode(id)!=null)
				return scaleY(state.getResourceNode(id).getYPosition()); 
			return -1;
		}
		public String getInfo() { 
			if(state.getUnit(id)!=null) {
				info = "ID: " + id;
				UnitView unit = state.getUnit(id);
				info += "\nHP: " + unit.getHP();
				if(unit.getTemplateView().canGather()) {
					if(unit.getCargoAmount()>0)
						info += "\n" + unit.getCargoType().toString() + ": " + unit.getCargoAmount();
				}
			}
			else {
				info = "";
				ResourceView resource = state.getResourceNode(id);
				info += resource.getType().toString() + ": " + resource.getAmountRemaining();
			}
			return info;
		}
	}
}
