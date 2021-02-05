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


	private StateView currentState;
	private HistoryView latestHistory;
	private DrawingContext latestContext;
	private int gameWorldTopLeftX;
	private int gameWorldTopLeftY;

	private VisualAgent agent;
	private GameDrawer gameDrawer;
	private int playernum;
	private int selectedID;  	// left clicked
	private int infoVisSelectedID; // double clicked
	private Info info;
	
    public GamePanel(VisualAgent agent, GameDrawer gameDrawer) {
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
        this.gameDrawer = gameDrawer;
        recalculateContext();
        if(agent!=null)
        	this.playernum = agent.getPlayerNumber();
        selectedID = -1;
        infoVisSelectedID = -1;
    }

    public void reset() {
    	selectedID = -1;
        infoVisSelectedID = -1;
    }
    
    public void recalculateContext() {
    	latestContext = new DrawingContext(agent == null ? -1 : agent.getPlayerNumber(), gameDrawer.getTopBarHeight(), gameWorldTopLeftX, gameWorldTopLeftY, getWidth(), getHeight(), SCALING_FACTOR);
    }
    
    public int convertPixelToGameX(int pixelX) {
    	return latestContext.convertPixelToGameWorldX(pixelX);
    }
    public int convertPixelToGameY(int pixelY) {
    	return latestContext.convertPixelToGameWorldY(pixelY);
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	if (currentState == null) {
    		return;
    	}
    	recalculateContext();
    	gameDrawer.drawBackground(latestContext, g);
        for (int x = 0; x < currentState.getXExtent(); x++) {
        	for (int y = 0; y < currentState.getYExtent(); y++) {
        		gameDrawer.drawTile(latestContext, g, x, y);
        	}
        }
      //Draw some lines
        g.setColor(new Color(0x66,0x44,0x22));
        for (int i = 0; i<=currentState.getXExtent(); i++) {
            g.drawLine(latestContext.convertGameWorldToPixelX(i), latestContext.convertGameWorldToPixelY(0), latestContext.convertGameWorldToPixelX(i), latestContext.convertGameWorldToPixelY(currentState.getYExtent()));
        }
        for (int j = 0; j<=currentState.getYExtent(); j++)
            g.drawLine(latestContext.convertGameWorldToPixelX(0), latestContext.convertGameWorldToPixelY(j), latestContext.convertGameWorldToPixelX(currentState.getXExtent()), latestContext.convertGameWorldToPixelY(j));
        
        gameDrawer.drawForeground(latestContext, g);
        
      //draw selected by left click
        if(selectedID>=0) {
        	UnitView unit = currentState.getUnit(selectedID);
        	if (unit != null)
        	{
	        	int x = latestContext.convertGameWorldToPixelX(unit.getXPosition());
	            int y = latestContext.convertGameWorldToPixelY(unit.getYPosition());
	            if(x >= 0 && y >= 0) {
	            	DrawingStrategy selected = DrawingStrategy.selectedGraphic();
	            	selected.draw(g, x, y);
	            }
        	}
        }
        
        
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
        
        gameDrawer.drawTopBar(latestContext, g);
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
				if(gameWorldTopLeftY > 0)
					gameWorldTopLeftY--;
				break;
			case DOWN:
				if(gameWorldTopLeftY + getHeight()/SCALING_FACTOR < currentState.getYExtent())
				gameWorldTopLeftY++;
				break;
			case LEFT:
				if(gameWorldTopLeftX > 0)
					gameWorldTopLeftX--;
				break;
			case RIGHT:
				if(gameWorldTopLeftX + getWidth()/SCALING_FACTOR < currentState.getXExtent())
					gameWorldTopLeftX++;	
				break;
            }
            recalculateContext();
            GamePanel.this.repaint();
        }

    }
    
	public void updateState(StateView state, HistoryView history) {
		this.gameDrawer.updateState(state, history);
		this.currentState = state;
		this.latestHistory = history;
        this.repaint();
	}
	
	private class GamePanelMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
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
			int x = latestContext.convertPixelToGameWorldX(e.getX());
			int y = latestContext.convertPixelToGameWorldY(e.getY());
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
		int x = latestContext.convertPixelToGameWorldX(e.getX());
		int y = latestContext.convertPixelToGameWorldY(e.getY());
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
		int x = latestContext.convertPixelToGameWorldX(e.getX());
		int y = latestContext.convertPixelToGameWorldY(e.getY());
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
				return latestContext.convertGameWorldToPixelX(state.getUnit(id).getXPosition());
			else if (state.getResourceNode(id)!=null)
				return latestContext.convertGameWorldToPixelX(state.getResourceNode(id).getXPosition());
			else
				return -1;
		}
		public int getY() {
			if(state.getUnit(id)!=null)
				return latestContext.convertGameWorldToPixelY(state.getUnit(id).getYPosition());
			else if (state.getResourceNode(id)!=null)
				return latestContext.convertGameWorldToPixelY(state.getResourceNode(id).getYPosition()); 
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
