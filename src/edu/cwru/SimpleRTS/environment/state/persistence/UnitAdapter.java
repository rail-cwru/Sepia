package edu.cwru.SimpleRTS.environment.state.persistence;

import java.util.Map;

import edu.cwru.SimpleRTS.environment.state.persistence.generated.XmlUnit;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;

public class UnitAdapter {

	@SuppressWarnings("rawtypes")
	private Map<Integer,Template> templates;
	
	public UnitAdapter(@SuppressWarnings("rawtypes") Map<Integer,Template> templates) {
		this.templates = templates;
	}
	
	public Unit fromXml(XmlUnit xml) {
		int templateId = xml.getTemplateID();
		UnitTemplate template = (UnitTemplate) templates.get(templateId);
		Unit unit = new Unit(template,xml.getID());
		unit.setxPosition(xml.getXPosition());
		unit.setyPosition(xml.getYPosition());
		unit.setTask(xml.getUnitTask());
		unit.pickUpResource(xml.getCargoType(), xml.getCargoAmount());
		unit.takeDamage(template.getBaseHealth()-xml.getCurrentHealth());
		//TODO - set production
		
		return unit;
	}
	
	public XmlUnit toXml(Unit unit) {
		XmlUnit xml = new XmlUnit();
		xml.setID(unit.ID);
		xml.setCurrentHealth(unit.getCurrentHealth());
		xml.setXPosition(unit.getxPosition());
		xml.setYPosition(unit.getyPosition());
		xml.setCargoType(unit.getCurrentCargoType());
		xml.setCargoAmount(unit.getCurrentCargoAmount());
		xml.setTemplateID(unit.getTemplate().ID);
		xml.setUnitTask(unit.getTask());
		xml.setProductionTemplateID(unit.getCurrentProductionID());
		xml.setProductionAmount(unit.getAmountProduced());
		
		return xml;
	}
}
