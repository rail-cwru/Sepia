package edu.cwru.SimpleRTS.action;

import java.io.Serializable;

import edu.cwru.SimpleRTS.model.Direction;
/**
 * The primary class of issued commands.
 * Contains factory methods for the construction of valid actions.
 * @author The Condor
 *
 */
public class Action implements Serializable{

	protected ActionType type;
	protected int unitId;
	protected Action(int untId, ActionType type)
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
	@Override public String toString()
	{
		return "Action: Unit "+unitId + ", Type " + type.toString();
	}
	
	/**
	 * This is a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to move.  When executed, it attempts to move in that direction, failing if another unit is already there.
	 * @param unitid Acting unit's ID
	 * @param d
	 * @return
	 */
	public static Action createPrimitiveMove(int unitid, Direction d) {
		return new DirectedAction(unitid, ActionType.PRIMITIVEMOVE, d);
	}
	/**
	 * This is a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to gather in.  When executed, it looks in the specified direction for a resource node, if there is a node, it moves resources from the node into the unit's inventory.
	 * @param unitid Acting unit's ID
	 * @param d
	 * @return
	 */
	public static Action createPrimitiveGather(int unitid, Direction d) {
		return new DirectedAction(unitid, ActionType.PRIMITIVEGATHER, d);
	}
	/**
	 * This is a LocatedAction, taking as parameters the acting unit's ID and the x and y coordinates of where to move.  When executed, it does PRIMITIVEMOVEs to reach that location.
	 * @param unitid Acting unit's ID
	 * @param x
	 * @param y
	 * @return
	 */
	public static Action createCompoundMove(int unitid, int x, int y) {
		return new LocatedAction(unitid, ActionType.COMPOUNDMOVE, x, y);
	}
	/**
	 * This is a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the unit or upgrade that you are trying to build.  When executed, it does PRIMITIVEPRODUCE until the unit or upgrade is completed.
	 * @param unitid Acting unit's ID
	 * @param templateID
	 * @return
	 */
	public static Action createCompoundProduction(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.COMPOUNDPRODUCE, templateID);
	}
	/**
	 * This is a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the unit or upgrade that you are trying to build.  When executed, it does one turn's work toward the creation of the unit or upgrade specified.  As all units and upgrades take one turn to make, this is enough to complete it.  If it is a unit being made, the new unit is put to one side after being made.
	 * @param unitid Acting unit's ID
	 * @param templateID
	 * @return
	 */
	public static Action createPrimitiveProduction(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.PRIMITIVEPRODUCE, templateID);
	}
	/**
	 * This is a LocatedProductionAction, taking as parameters the acting unit's ID and the ID of the template of the building that you are trying to build, as well as x and y coordinates of where to build it.  When executed, it uses repeated PRIMITIVEMOVEs to reach the specified location, then does PRIMITIVEBUILD until the building is done.
	 * @param unitid Acting unit's ID
	 * @param templateID
	 * @param x
	 * @param y
	 * @return
	 */
	public static Action createCompoundBuild(int unitid, int templateID, int x, int y) {
		return new LocatedProductionAction(unitid, ActionType.COMPOUNDBUILD, templateID,x,y);
	}
	/**
	 * This is a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the building that you are trying to build.  When executed, it does one turn's work toward building that kind of building on the spot the unit is at.  As all buildings take one turn to build, this is enough to complete it, making a building on the spot and moving the builder off to one side.
	 * @param unitid Acting unit's ID Acting unit's ID
	 * @param templateID Template ID of the building type to build
	 * @return
	 */
	public static Action createPrimitiveBuild(int unitid, int templateID) {
		return new ProductionAction(unitid, ActionType.PRIMITIVEBUILD, templateID);
	}
	/**
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the target's ID.  When executed, it moves into range of a unit, then attacks it once.
	 * @param unitid Acting unit's ID
	 * @param targetid
	 * @return
	 */
	public static Action createCompoundAttack(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.COMPOUNDATTACK, targetid);
	}
	/**
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the target's ID.  When executed, it attempts to attack the targeted unit, failing when it out of range.
	 * @param unitid Acting unit's ID
	 * @param targetid
	 * @return
	 */
	public static Action createPrimitiveAttack(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.PRIMITIVEATTACK, targetid);
	}
	/**
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the ID of a resource node to gather from.  When executed, it does PRIMITIVEMOVEs until next to the specified node, then performs a PRIMITIVEGATHER on it.
	 * @param unitid Acting unit's ID
	 * @param targetid
	 * @return
	 */
	public static Action createCompoundGather(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.COMPOUNDGATHER, targetid);
	}
	/**
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the ID of a town hall to deposit at.  When executed, it does PRIMITIVEMOVEs until next to the specified town hall, then performs a PRIMITIVEDEPOSIT on it.
	 * @param unitid Acting unit's ID
	 * @param targetid
	 * @return
	 */
	public static Action createCompoundDeposit(int unitid, int targetid) {
		return new TargetedAction(unitid, ActionType.COMPOUNDDEPOSIT, targetid);
	}
	/**
	 * This is a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to deposit in.  When executed, it looks in the specified direction for a town hall, if there is a town hall that you control, it moves resources from the unit's inventory and gives them to you.
	 * @param unitid Acting unit's ID
	 * @param d
	 * @return
	 */
	public static Action createPrimitiveDeposit(int unitid, Direction d) {
		return new DirectedAction(unitid, ActionType.PRIMITIVEDEPOSIT, d);
	}
	/**
	 * Used internally to indicate that a compound action failed, and it should be redone
	 * @param unitid Acting unit's ID
	 * @return
	 */
	public static Action createFail(int unitid) {
		return new Action(unitid, ActionType.FAILED);
	}
	/**
	 * Used internally to indicate that a compound action failed permanently, and it should be aborted
	 * @param unitid Acting unit's ID
	 * @return
	 */
	public static Action createPermanentFail(int unitid) {
		return new Action(unitid, ActionType.FAILEDPERMANENTLY);
	}
}
