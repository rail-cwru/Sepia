package edu.cwru.SimpleRTS.action;

public enum ActionType {
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to move.  When executed, it attempts to move in that direction, failing if another unit is already there.
	 */
	PRIMITIVEMOVE, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a TargetedAction, taking as parameters the acting unit's ID and the target's ID.  When executed, it attempts to attack the targeted unit, failing when it out of range.
	 */
	PRIMITIVEATTACK, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to gather in.  When executed, it looks in the specified direction for a resource node, if there is a node, it moves resources from the node into the unit's inventory.
	 */
	PRIMITIVEGATHER, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a DirectedAction, taking as parameters the acting unit's ID and a direction to attempt to deposit in.  When executed, it looks in the specified direction for a town hall, if there is a town hall that you control, it moves resources from the unit's inventory and gives them to you.
	 */
	PRIMITIVEDEPOSIT, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the building that you are trying to build.  When executed, it does one turn's work toward building that kind of building on the spot the unit is at.  If it completes the build, it will make a building on the spot and moving the builder off to one side.
	 */
	PRIMITIVEBUILD, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the unit or upgrade that you are trying to build.  When executed, it does one turn's work toward the creation of the unit or upgrade specified.  If it is a unit being made, the new unit is put to one side after being made.
	 */
	PRIMITIVEPRODUCE,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a LocatedAction, taking as parameters the acting unit's ID and the x and y coordinates of where to move.  When executed, it does PRIMITIVEMOVEs to reach that location.
	 */
	COMPOUNDMOVE, 
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a TargetedAction, taking as parameters the acting unit's ID and the target's ID.  When executed, it moves into range of a unit, then attacks it once.
	 */
	COMPOUNDATTACK,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the ID of a resource node to gather from.  When executed, it does PRIMITIVEMOVEs until next to the specified node, then performs a PRIMITIVEGATHER on it.
	 */
	COMPOUNDGATHER,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is a TargetedAction, taking as parameters the acting unit's ID and the ID of a town hall to deposit at.  When executed, it does PRIMITIVEMOVEs until next to the specified town hall, then performs a PRIMITIVEDEPOSIT on it.
	 */
	COMPOUNDDEPOSIT,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a LocatedProductionAction, taking as parameters the acting unit's ID and the ID of the template of the building that you are trying to build, as well as x and y coordinates of where to build it.  When executed, it uses repeated PRIMITIVEMOVEs to reach the specified location, then does PRIMITIVEBUILD until the building is done.
	 */
	COMPOUNDBUILD,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * This is for a ProductionAction, taking as parameters the acting unit's ID and the ID of the template of the unit or upgrade that you are trying to build.  When executed, it does PRIMITIVEPRODUCE until the unit or upgrade is completed.
	 */
	COMPOUNDPRODUCE,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * Used internally to indicate a failure that requires recalculating a compound action
	 */
	FAILED,
	/**
	 * Please use the static factory methods in edu.cwru.SimpleRTS.action.Action to construct actions.
	 * Used internally to indicate a failure of a compound action that can not be fixed by recalculating.
	 */
	FAILEDPERMANENTLY;
}
