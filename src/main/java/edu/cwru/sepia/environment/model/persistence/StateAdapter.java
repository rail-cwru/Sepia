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

import java.util.List;

import edu.cwru.sepia.environment.model.persistence.generated.XmlPlayer;
import edu.cwru.sepia.environment.model.persistence.generated.XmlResourceNode;
import edu.cwru.sepia.environment.model.persistence.generated.XmlState;
import edu.cwru.sepia.environment.model.state.PlayerState;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateBuilder;

public class StateAdapter {
	
	PlayerAdapter playerAdapter = new PlayerAdapter();
	ResourceNodeAdapter resourceNodeAdapter = new ResourceNodeAdapter();

	public XmlState toXml(State state) {
		XmlState xml = new XmlState();
		
		List<XmlPlayer> players = xml.getPlayer();
		for(PlayerState ps : state.getPlayerStates())
		{
			players.add(playerAdapter.toXml(ps));
		}
		
		List<XmlResourceNode> resources = xml.getResourceNode();
		for(ResourceNode rn : state.getResources())
		{
			resources.add(resourceNodeAdapter.toXml(rn));
		}
		
		xml.setXExtent(state.getXExtent());		
		xml.setYExtent(state.getYExtent());
		xml.setNextTemplateID(state.getNextTemplateIDForXMLSave());
		xml.setNextTargetID(state.getNextTargetIDForXMLSave());
		xml.setFogOfWar(state.getFogOfWar());
//		xml.setRevealedResources(state.getRevealedResources());
		return xml;
	}
	
	public State fromXml(XmlState xml) {
		StateBuilder builder = new StateBuilder();
		
		for(XmlPlayer player : xml.getPlayer())
		{
			builder.addPlayer(playerAdapter.fromXml(player));
		}
		
		
		if(xml.getResourceNode() != null)
		{
			for(XmlResourceNode resource : xml.getResourceNode())
			{
				builder.addResource(resourceNodeAdapter.fromXml(resource));
			}
		}
		
		builder.setSize(xml.getXExtent(), xml.getYExtent());
		builder.setIDDistributerTargetMax(xml.getNextTargetID());
		builder.setIDDistributerTemplateMax(xml.getNextTemplateID());
		State state = builder.build();
		state.updateGlobalListsFromPlayers();
		state.recalculateVision();
		state.setFogOfWar(xml.isFogOfWar());
//		state.setRevealedResources(xml.isRevealedResources());
		return state;
	}
}
