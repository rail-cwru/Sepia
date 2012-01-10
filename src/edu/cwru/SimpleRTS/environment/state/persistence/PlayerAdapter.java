package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.Collection;
import java.util.List;

import edu.cwru.SimpleRTS.environment.PlayerState;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class PlayerAdapter {
	

	public XmlPlayer toXml(PlayerState player) {
		XmlPlayer xml = new XmlPlayer();
		UnitAdapter unitAdapter = new UnitAdapter(player.getTemplates());
		
		xml.setID(player.playerNum);
		
		List<XmlUnit> units = xml.getUnit();
		for(Unit u : player.getUnits().values())
		{
			units.add(unitAdapter.toXml(u));
		}
		
		List<Integer> upgrades = xml.getUpgrade();
		upgrades.addAll(player.getUpgrades());
		
		List<Integer> templateIds = xml.getTemplate();
		templateIds.addAll(player.getTemplates().keySet());
				
		List<XmlResourceQuantity> resources = xml.getResourceAmount();
		for(ResourceType type : ResourceType.values())
		{
			XmlResourceQuantity quantity = new XmlResourceQuantity();
			quantity.setType(type);
			quantity.setQuantity(player.getCurrentResourceAmount(type));
			resources.add(quantity);
		}

		xml.setSupply(player.getCurrentSupply());
		xml.setSupplyCap(player.getCurrentSupplyCap());
		
		return xml;
	}
	
	public PlayerState fromXml(XmlPlayer xml, @SuppressWarnings("rawtypes") Collection<Template> templates) {
		PlayerState player = new PlayerState(xml.getID());
		UnitAdapter unitAdapter = new UnitAdapter(player.getTemplates());
		
		for(XmlUnit unit : xml.getUnit())
		{
			player.addUnit(unitAdapter.fromXml(unit));
		}
		
		player.getUpgrades().addAll(xml.getUpgrade());
		
		for(int id : xml.getTemplate())
		{
			for(@SuppressWarnings("rawtypes") Template template : templates)
			{
				if(template.ID == id)
				{
					player.addTemplate(template);
					break;
				}
			}
		}
		
		for(XmlResourceQuantity resource : xml.getResourceAmount())
		{
			player.setCurrentResourceAmount(resource.getType(), resource.getQuantity());
		}
		
		player.setCurrentSupply(xml.getSupply());
		player.setCurrentSupplyCap(xml.getSupplyCap());
		
		return player;
	}
}
