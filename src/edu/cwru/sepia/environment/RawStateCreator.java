package edu.cwru.sepia.environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.cwru.sepia.environment.State.StateView;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.Template.TemplateView;
import edu.cwru.sepia.model.resource.ResourceNode;
import edu.cwru.sepia.model.resource.ResourceType;
import edu.cwru.sepia.model.resource.ResourceNode.ResourceView;
import edu.cwru.sepia.model.unit.Unit;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.model.unit.Unit.UnitView;
import edu.cwru.sepia.model.unit.UnitTemplate.UnitTemplateView;
import edu.cwru.sepia.model.upgrade.UpgradeTemplate;
import edu.cwru.sepia.model.upgrade.UpgradeTemplate.UpgradeTemplateView;
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
