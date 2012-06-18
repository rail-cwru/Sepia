package edu.cwru.SimpleRTS.action;

/**
 * A subtype of Action, includes CompoundProduction, PrimitiveProduction, PrimitiveBuild
 *
 */
public class ProductionAction extends Action {	
	private static final long serialVersionUID = -2225942140919623162L;
	
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
	public String toString() 
	{
		return "ProductionAction [templateid=" + templateid + ", type=" + type
				+ ", unitId=" + unitId + "]";
	}
	
	@Override 
	public boolean equals(Object other)
	{
		if (this == other)
		{
			return true;
		}
		else if (other == null || !this.getClass().equals(other.getClass()))
		{
			return false;
		}
		else
		{
			
			ProductionAction aother = (ProductionAction)other;
			return aother.type == type && aother.unitId == unitId && aother.templateid == templateid;
		}
	}
	
	@Override 
	public int hashCode()
	{
		int prime = 61;
		return prime*prime * templateid + prime * type.hashCode() + unitId;
	}
}
