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
package edu.cwru.sepia.environment.state.persistence;

import java.util.Map;

import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.environment.model.state.UnitTemplate;
import edu.cwru.sepia.environment.state.persistence.generated.XmlUnit;

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
		unit.setCargo(xml.getCargoType(), xml.getCargoAmount());
		unit.setHP(xml.getCurrentHealth());
		unit.setDurativeStatus(ActionAdapter.fromXml(xml.getProgressPrimitive()), xml.getProgressAmount());
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
		xml.setProgressPrimitive(ActionAdapter.toXml(unit.getActionProgressPrimitive()));
		xml.setProgressAmount(unit.getActionProgressAmount());
		
		return xml;
	}
}
