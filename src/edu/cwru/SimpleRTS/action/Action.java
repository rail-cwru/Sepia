package edu.cwru.SimpleRTS.action;

import edu.cwru.SimpleRTS.model.Direction;
import edu.cwru.SimpleRTS.model.unit.Unit;

public class Action {

	protected ActionType type;
	protected int unitId;
	public Action(int untId, ActionType type)
	{
		this.type = type;
		this.unitId = untId;
	}
	public int getUnitId() {
		return unitId;
	}
	public ActionType getType()
	{
		return type;
	}
	@Override
	public String toString()
	{
		return "Action: Unit "+unitId + ", Type " + type.toString();
	}
	public static Action createPrimitiveMove(int unitid, Direction d) {
		return new DirectedAction(unitid, ActionType.PRIMITIVEMOVE, d);
	}
	public static Action createPrimitiveGather(int unitid, Direction d) {
		return new DirectedAction(unitid, ActionType.PRIMITIVEGATHER, d);
	}

	public static Action createCompoundMove(int unitid, int x, int y) {
		return new LocatedAction(unitid, ActionType.COMPOUNDMOVE, x, y);
	}
	public static Action createCompoundProduction(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.COMPOUNDPRODUCE, templateID);
	}
	public static Action createCompoundUpgrade(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.COMPOUNDUPGRADE, templateID);
	}
	public static Action createPrimitiveProduction(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.PRIMITIVEPRODUCE, templateID);
	}
	public static Action createPrimitiveUpgrade(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.PRIMITIVEUPGRADE, templateID);
	}
	public static Action createCompoundBuild(int unitid, int templateID, int x, int y) {
		return new LocatedProductionAction(unitid, ActionType.COMPOUNDBUILD, templateID,x,y);
	}
	public static Action createPrimitiveBuild(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.PRIMITIVEBUILD, templateID);
	}
	public static Action createCompoundAttack(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.COMPOUNDATTACK, targetid);
	}
	public static Action createCompoundGather(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.COMPOUNDGATHER, targetid);
	}
	
}
