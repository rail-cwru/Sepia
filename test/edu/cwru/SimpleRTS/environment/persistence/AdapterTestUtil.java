package edu.cwru.SimpleRTS.environment.persistence;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.junit.BeforeClass;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlPlayer;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlResourceQuantity;
import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.resource.ResourceType;
import edu.cwru.SimpleRTS.model.unit.UnitTask;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.util.TypeLoader;

public class AdapterTestUtil {

	@SuppressWarnings("rawtypes")
	@BeforeClass
	public static Map<Integer,Template> loadTemplates() throws FileNotFoundException, JSONException {
		Map<Integer,Template> templates = new HashMap<Integer,Template>();
		List<UnitTemplate> templateList = TypeLoader.loadUnitsFromFile("data/unit_templates", 0);
		for(Template t : templateList)
		{
			templates.put(t.ID,t);
		}
		return templates;
	}
	
	public static XmlUnit createExampleUnit() {
		XmlUnit xml = new XmlUnit();
		xml.setCargoAmount(10);
		xml.setCargoType(ResourceType.GOLD);
		xml.setCurrentHealth(10);
		xml.setProductionAmount(1);
		xml.setProductionTemplateID(4);
		xml.setTemplateID(2);
		xml.setUnitTask(UnitTask.Build);
		xml.setXPosition(2);
		xml.setYPosition(3);
		return xml;
	}
	
	public static XmlPlayer createExamplePlayer() {
		XmlPlayer xml = new XmlPlayer();
		
		xml.getUnit().add(createExampleUnit());
		xml.getUnit().add(createExampleUnit());
		xml.getUnit().add(createExampleUnit());
		
		xml.setID(0);
		xml.setSupply(1);
		xml.setSupplyCap(5);
		
		XmlResourceQuantity gold = new XmlResourceQuantity();
		gold.setType(ResourceType.GOLD);
		gold.setQuantity(700);
		xml.getResourceAmount().add(gold);
		
		XmlResourceQuantity wood = new XmlResourceQuantity();
		wood.setType(ResourceType.WOOD);
		wood.setQuantity(700);		
		xml.getResourceAmount().add(wood);
		
		for(int i = 0; i < 6; i++)
		{
			xml.getTemplate().add(i);
		}
		
		xml.getUpgrade().add(0);
		
		return xml;
	}
}
