% This function will be called at the first step of each episode
% a map (unitid -> action) will be return indicating which action to be taken by each unit.
function [ action ] = agent_initialStep( state )

action = agent_middleStep(state);

end

