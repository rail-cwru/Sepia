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
package edu.cwru.sepia.environment.model.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template.TemplateView;
/**
 * A core component of simulating. <br>
 * RawStateCreator is used to repeatedly clone an existing state from an array of bytes representing a serialization.
 * @author The Condor
 *
 */
public class RawStateCreator implements StateCreator{
	private static final long	serialVersionUID	= 1L;
	byte[] stateData;
	public RawStateCreator(byte[] stateData) throws IOException {
		this.stateData = new byte[stateData.length];
		System.arraycopy(stateData, 0, this.stateData, 0, stateData.length);
		
	}
	@Override
	public State createState() {
		
		
		try {
			//You may be tempted to cache this and reset the stream every time
			//RESIST THAT TEMPTATION
			//It will cause wierd things to happen to links back to the state.  But only the second time, making it hard to find.
			//This can make a stateview (which presently stores the state) to loop back to a different state than the one that stores them, so they don't get updated.
			//In other words, if you cached the streams and reset them, then created two states, any preexiting views in the first state would link to the first state (good), but preexisting views in the second state would be updated with the first state
			ByteArrayInputStream bis = new ByteArrayInputStream(stateData);
			ObjectInputStream stateStream = new ObjectInputStream(bis);
			return (State)stateStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("The data in the State cannot be read as a state.");
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to properly read stored State");
		}
		
		
//		
//		state.setFogOfWar(view.isFogOfWar());
//		state.setSize(view.getXExtent(),view.getYExtent());
//		for (Integer player : view.getPlayerNumbers())
//		{
//			state.addPlayer(player);
//			state.view.getSupplyAmount(player);
//		view.getSupplyCap(player);
//		for (ResourceType rt : ResourceType.values())
//		{
//			state.depositResources(player, rt,view.getResourceAmount(player, rt));
//		}
//		}
//		for (Integer id : view.getAllTemplateIds())
//		{
//			TemplateView templateview = view.getTemplate(id);
//			Template toAdd;
//			if (templateview instanceof UpgradeTemplateView)
//			{
//				UpgradeTemplate temp = new UpgradeTemplate(templateview.getID());
//				UpgradeTemplateView tempview = (UpgradeTemplateView)templateview;
//				tempview.getAffectedUnitTypes()
//				tempview.getArmorChange()
//				tempview.getBasicAttackChange()
//				tempview.getHealthChange()
//				tempview.getPiercingAttackChange()
//				tempview.getRangeChange()
//				tempview.getSightRangeChange()
//				toAdd = temp;
//			}
//			else if (templateview instanceof UnitTemplateView)
//			{
//				UnitTemplate temp = new UnitTemplate(templateview.getID());
//				UnitTemplateView tempview = (UnitTemplateView)templateview;
//				tempview.canAcceptGold()
//				tempview.canAcceptWood()
//				tempview.canBuild()
//				tempview.canGather()
//				tempview.canMove()
//				tempview.getArmor()
//				tempview.getBaseHealth()
//				tempview.getBasicAttack()
//				tempview.getCharacter()
//				tempview.getDurationAttack()
//				tempview.getDurationDeposit()
//				tempview.getDurationGatherGold()
//				tempview.getDurationGatherWood()
//				tempview.getDurationMove()
//				tempview.getFoodProvided()
//				tempview.getPiercingAttack()
//				tempview.getRange()
//				tempview.getSightRange()
//				for (Integer id2 : view.getAllTemplateIds())
//				{
//					if (tempview.canProduce(id2))
//					{
//						temp.
//					}
//				}
//				
//				toAdd = temp;
//			}
//			else
//			{
//				throw new IllegalArgumentException("view contains illegal state type "+templateview.getClass());
//			}
//			//Add non type-specific fields
//			toAdd.setFoodCost(templateview.getFoodCost());
//			toAdd.setGoldCost(templateview.getGoldCost());
//			toAdd.setWoodCost(templateview.getWoodCost());
//			toAdd.setTimeCost(templateview.getTimeCost());
//			toAdd.setPlayer(templateview.getPlayer());
//			toAdd.setName(templateview.getName());
//			prerequisites;
//			state.addTemplate(toAdd);
//		}
//		for (Integer id : view.getAllUnitIds())
//		{
//			UnitView unitview = view.getUnit(id);
//			Unit unit = new Unit((UnitTemplate)state.getTemplate(unitview.getTemplateView().getID()),unitview.getID());
//			unitview.getXPosition()
//			unitview.getCurrentDurativeAction()
//			unitview.getCurrentDurativeProgress()
//			unitview.getCargoAmount()
//			unitview.getCargoType()
//			unitview.getHP()
//			unitview.getTask()
//		}
//		
//		for (Integer id : view.getAllResourceIds())
//		{
//			ResourceView resourceview = view.getResourceNode(id);
//			ResourceNode toAdd = new ResourceNode(resourceview.getType(),resourceview.getXPosition(),resourceview.getYPosition(),resourceview.getAmountRemaining(),resourceview.getID());
//		}
//		
//		
//		
//		
//		
//		
	}
	
}
