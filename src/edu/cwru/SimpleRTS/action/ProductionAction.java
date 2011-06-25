package edu.cwru.SimpleRTS.action;

public class ProductionAction extends Action
{
	int templateid;
	public ProductionAction(int unitid, ActionType type, int templateid)
	{
		super(unitid, type);
		this.templateid = templateid;
	}
	public int getTemplateId()
	{
		return templateid;
	}
	@Override
	public String toString() {
		return "ProductionAction [templateid=" + templateid + ", type=" + type
				+ ", unitId=" + unitId + "]";
	}
}
