package edu.cwru.SimpleRTS.environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.cwru.SimpleRTS.environment.State.StateView;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.Template.TemplateView;
import edu.cwru.SimpleRTS.model.resource.ResourceNode;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.ResourceView;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.Unit.UnitView;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate.UnitTemplateView;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate.UpgradeTemplateView;
/**
 * A core component of simulating 
 * @author The Condor
 *
 */
public class RawStateCreator implements StateCreator{
	private static final long	serialVersionUID	= 1L;
	private ObjectInputStream stateStream;
	public RawStateCreator(byte[] stateData) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(stateData);
		ObjectInputStream stateStream = new ObjectInputStream(bis);
		stateStream.mark(Integer.MAX_VALUE);
		
	}
	@Override
	public State createState() {
		
		
		try {
			stateStream.reset();
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
