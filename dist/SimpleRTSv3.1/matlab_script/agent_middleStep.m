% This function will be called at every step (except for the first and terminal state).
% a map (unitid -> action) will be return indicating which action to be taken by each unit.
function [ action ] = agent_middleStep( state )

% This agent will first collect gold to produce a peasant
% then the two peasants will collect gold and wood separately until reach
% goal

import edu.cwru.SimpleRTS.action.*;
import edu.cwru.SimpleRTS.environment.*;
import edu.cwru.SimpleRTS.model.*;
import edu.cwru.SimpleRTS.model.resource.*;
import edu.cwru.SimpleRTS.model.resource.ResourceNode.*;
import edu.cwru.SimpleRTS.model.unit.*;

action = java.util.HashMap();

global playernum;
global woodRequired;
global goldRequired;

currentGold = state.getResourceAmount(0, ResourceType.GOLD).intValue();
currentWood = state.getResourceAmount(0, ResourceType.WOOD).intValue();
allUnitIDs = state.getAllUnitIds();
peasantIDs = zeros(allUnitIDs.size, 1)-1;
townHallIDs = zeros(allUnitIDs.size, 1)-1;
for i=0:allUnitIDs.size-1
    id = allUnitIDs.get(i);
    unit = state.getUnit(id);
    unitTypeName = unit.getTemplateView.getUnitName;
    if(strcmp(unitTypeName,'TownHall'))
        townHallIDs(i+1) = allUnitIDs.get(i);
    end
    if(strcmp(unitTypeName, 'Peasant'))
        peasantIDs(i+1) = allUnitIDs.get(i);
    end
end
peasantIDs = peasantIDs(peasantIDs>-1);
peasantNum = size(peasantIDs, 1);
townHallIDs = townHallIDs(townHallIDs>-1);
townHallID = townHallIDs(1);

if(peasantNum>=2) % collect resources
    if(currentWood<woodRequired)
        peasantID = peasantIDs(2);
        if(state.getUnit(peasantID).getCargoAmount>0)
            b = TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townHallID);
        else
            gm_enum = javaMethod('valueOf', 'edu.cwru.SimpleRTS.model.resource.ResourceNode$Type', 'TREE');
            resourceIDs = state.getResourceNodeIds(gm_enum);
            b = TargetedAction(peasantID, ActionType.COMPOUNDGATHER, resourceIDs.get(0));
        end
        action.put(peasantID, b);
    elseif(currentGold<goldRequired)
        peasantID = peasantIDs(1);
        if(state.getUnit(peasantID).getCargoType==ResourceType.GOLD && state.getUnit(peasantID).getCargoAmount>0)
            b = TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townHallID);
        else
            gm_enum = javaMethod('valueOf', 'edu.cwru.SimpleRTS.model.resource.ResourceNode$Type', 'GOLD_MINE');
            resourceIDs = state.getResourceNodeIds(gm_enum);
            b = TargetedAction(peasantID, ActionType.COMPOUNDGATHER, resourceIDs.get(0));
        end
        action.put(peasantID, b);
    end
else % build peasant
    if(currentGold>=400)
        display 'Already have enough gold to produce a new peasant';
        peasanttemplate = state.getTemplate(playernum, 'Peasant');
        peasanttemplateID = peasanttemplate.getID;
        action.put(townHallID, Action.createCompoundProduction(townHallID, peasanttemplateID));
    else
        peasantID = peasantIDs(1);
        if(state.getUnit(peasantID).getCargoType==ResourceType.GOLD && state.getUnit(peasantID).getCargoAmount>0)
            b = TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townHallID);
        else
            gm_enum = javaMethod('valueOf', 'edu.cwru.SimpleRTS.model.resource.ResourceNode$Type', 'GOLD_MINE');
            resourceIDs = state.getResourceNodeIds(gm_enum);
            b = TargetedAction(peasantID, ActionType.COMPOUNDGATHER, resourceIDs.get(0));
        end
        action.put(peasantID, b);
    end
end

end

