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
package edu.cwru.sepia.environment.model.persistence;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.LocatedAction;
import edu.cwru.sepia.action.LocatedProductionAction;
import edu.cwru.sepia.action.ProductionAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlDirectedAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlLocatedAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlLocatedProductionAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlProductionAction;
import edu.cwru.sepia.environment.model.persistence.generated.XmlTargetedAction;

/**
 * An adapter between Action and it's subclasses and XmlAction and it's subclasses.
 * 
 * @author The Condor
 *
 */
public class ActionAdapter {
	//This class is kind of hackish because xml is an afterthought.
	@SuppressWarnings("rawtypes")
	
	public static Action fromXml(XmlAction xml) {
		if (xml == null)
			return null;
		Class c = xml.getClass();
		if (c.equals(XmlDirectedAction.class))
		{
			XmlDirectedAction txml = (XmlDirectedAction)xml;
			return new DirectedAction(txml.getUnitId(),txml.getActionType(),txml.getDirection());
		}
		else if (c.equals(XmlProductionAction.class))
		{
			XmlProductionAction txml = (XmlProductionAction)xml;
			return new ProductionAction(txml.getUnitId(),txml.getActionType(),txml.getTemplateId());
		}
		else if (c.equals(XmlTargetedAction.class))
		{
			XmlTargetedAction txml = (XmlTargetedAction)xml;
			return new TargetedAction(txml.getUnitId(),txml.getActionType(),txml.getTargetId());
		}
		else if (c.equals(XmlLocatedAction.class))
		{
			XmlLocatedAction txml = (XmlLocatedAction)xml;
			return new LocatedAction(txml.getUnitId(),txml.getActionType(),txml.getX(),txml.getY());
		}
		else if (c.equals(XmlLocatedProductionAction.class))
		{
			XmlLocatedProductionAction txml = (XmlLocatedProductionAction)xml;
			return new LocatedProductionAction(txml.getUnitId(),txml.getActionType(),txml.getTemplateId(),txml.getX(),txml.getY());
		}
		else if (c.equals(XmlAction.class))
		{
			return new Action(xml.getUnitId(),xml.getActionType());
		}
		else
		{
			throw new RuntimeException("The class "+c+" is not supported in loading xml");
		}
	}
	
	public static XmlAction toXml(Action action) {
		if (action == null)
			return null;
		XmlAction xml;
		Class<? extends Action> c = action.getClass();
		if (c.equals(DirectedAction.class))
		{
			XmlDirectedAction txml = new XmlDirectedAction();
			DirectedAction tact = (DirectedAction)action;
			txml.setDirection(tact.getDirection());
			xml = txml;
		}
		else if (c.equals(ProductionAction.class))
		{
			XmlProductionAction txml = new XmlProductionAction();
			ProductionAction tact = (ProductionAction)action;
			txml.setTemplateId(tact.getTemplateId());
			xml = txml;
		}
		else if (c.equals(TargetedAction.class))
		{
			XmlTargetedAction txml = new XmlTargetedAction();
			TargetedAction tact = (TargetedAction)action;
			txml.setTargetId(tact.getTargetId());
			xml = txml;
		}
		else if (c.equals(LocatedAction.class))
		{
			XmlLocatedAction txml = new XmlLocatedAction();
			LocatedAction tact = (LocatedAction)action;
			txml.setX(tact.getX());
			txml.setY(tact.getY());
			xml = txml;
		}
		else if (c.equals(LocatedProductionAction.class))
		{
			XmlLocatedProductionAction txml = new XmlLocatedProductionAction();
			LocatedProductionAction tact = (LocatedProductionAction)action;
			txml.setX(tact.getX());
			txml.setY(tact.getY());
			txml.setTemplateId(tact.getTemplateId());
			xml = txml;
			
		}
		else if (c.equals(Action.class))
		{
			XmlAction txml = new XmlAction();
			xml = txml;
			
		}
		else
		{
			throw new RuntimeException("The class "+c+" is not supported in saving to xml");
		}
		xml.setUnitId(action.getUnitId());
		xml.setActionType(action.getType());
		return xml;
	}
}
