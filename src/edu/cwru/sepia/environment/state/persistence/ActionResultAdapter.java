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

import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResult;
import edu.cwru.sepia.environment.state.persistence.generated.XmlActionResultLogger;
import edu.cwru.sepia.log.ActionResultLogger;

public class ActionResultAdapter {
	public static XmlActionResult toXml(ActionResult actionResult) {
		XmlActionResult toReturn = new XmlActionResult();
		toReturn.setAction(ActionAdapter.toXml(actionResult.getAction()));
		toReturn.setFeedback(actionResult.getFeedback());
		return toReturn;
	}
	
	public static ActionResult fromXml(XmlActionResult xml) {
		return new ActionResult(ActionAdapter.fromXml(xml.getAction()), xml.getFeedback()); 
	}
}
