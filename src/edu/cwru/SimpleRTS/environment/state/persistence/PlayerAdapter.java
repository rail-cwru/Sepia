package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.environment.PlayerState;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnitTemplate;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUpgradeTemplate;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

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
		
		List<XmlTemplate> xmltemplates= xml.getTemplate();
		for (Template t : player.getTemplates().values())
		{
			if (t instanceof UnitTemplate)
				xmltemplates.add(UnitTemplateAdapter.toXml((UnitTemplate)t));
			else if (t instanceof UpgradeTemplate)
				xmltemplates.add(UpgradeTemplateAdapter.toXml((UpgradeTemplate)t));
		}
				
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
	
	public PlayerState fromXml(XmlPlayer xml) {
		PlayerState player = new PlayerState(xml.getID());
		
		
		if(xml.getUpgrade() != null)
		{
			player.getUpgrades().addAll(xml.getUpgrade());
		}
		List <UnitTemplate> myunittemplates = new ArrayList<UnitTemplate>();
		List <UpgradeTemplate> myupgradetemplates = new ArrayList<UpgradeTemplate>();
		if(xml.getTemplate() != null)
		{
			for(XmlTemplate t : xml.getTemplate())
			{
				if (t instanceof XmlUnitTemplate)
					myunittemplates.add(UnitTemplateAdapter.fromXml((XmlUnitTemplate)t,player.playerNum));
				else if (t instanceof XmlUpgradeTemplate)
					myupgradetemplates.add(UpgradeTemplateAdapter.fromXml((XmlUpgradeTemplate)t,player.playerNum));
			}
		}
		for (UnitTemplate t : myunittemplates)
		{
			t.namesToIds(myunittemplates,myupgradetemplates);
			player.addTemplate(t);
		}
		for (UpgradeTemplate t : myupgradetemplates)
		{
			t.namesToIds(myunittemplates,myupgradetemplates);
			player.addTemplate(t);
		}
		
		
		UnitAdapter unitAdapter = new UnitAdapter(player.getTemplates());
		
		if(xml.getUnit() != null)
		{
			for(XmlUnit unit : xml.getUnit())
			{
				player.addUnit(unitAdapter.fromXml(unit));
			}
		}
		if(xml.getResourceAmount() != null)
		{
			for(XmlResourceQuantity resource : xml.getResourceAmount())
			{
				player.setCurrentResourceAmount(resource.getType(), resource.getQuantity());
			}
		}
		
		player.setCurrentSupply(xml.getSupply());
		player.setCurrentSupplyCap(xml.getSupplyCap());
		
		return player;
	}
}
