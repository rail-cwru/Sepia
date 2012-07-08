% This function will be called at every step (except for the first and terminal state).
% a map (unitid -> action) will be return indicating which action to be taken by each unit.
function [ action ] = agent_middleStep( state )

% This agent will first collect gold to produce a peasant
% then the two peasants will collect gold and wood separately until reach
% goal

import edu.cwru.sepia.action.*;
import edu.cwru.sepia.environment.*;
import edu.cwru.sepia.model.*;
import edu.cwru.sepia.model.resource.*;
import edu.cwru.sepia.environment.model.state.ResourceNode.*;
import edu.cwru.sepia.model.unit.*;

action = java.util.HashMap();

global playernum;

allUnitIDs = state.getAllUnitIds();
footmanID = -1;
townHallID = -1;
for i=0:allUnitIDs.size-1
    id = allUnitIDs.get(i);
    unit = state.getUnit(id);
    unitTypeName = unit.getTemplateView.getUnitName;
    if(strcmp(unitTypeName,'TownHall'))
        townHallID = id;
    end
    if(strcmp(unitTypeName, 'Footman'))
        footmanID = id;
    end
end

b = TargetedAction(footmanID, ActionType.COMPOUNDATTACK, townHallID);
action.put(footmanID, b);

end

