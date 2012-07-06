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
