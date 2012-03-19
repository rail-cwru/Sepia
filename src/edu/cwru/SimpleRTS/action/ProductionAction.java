package edu.cwru.SimpleRTS.action;

public class ProductionAction extends Action
{
	private final int templateid;
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
	@Override public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		else if (!(other instanceof ProductionAction))
		{
			return false;
		}
		else
		{
			
			ProductionAction aother = (ProductionAction)other;
			return super.equals(aother) && aother.templateid == templateid;
		}
	}
	@Override public int hashCode()
	{
		int prime = 61;
		return prime * templateid + super.hashCode();
	}
}
