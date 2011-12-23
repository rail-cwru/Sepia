% In this script you initialize the variables to be used for your agent.
% This script will be called only one time at the beginning of the program.
function [] = agent_init(arg_playernum)

import java.util.prefs.*;

global playernum;
global goldRequired;
global woodRequired;

playernum = arg_playernum;
prefs = Preferences.userRoot.node('edu').node('cwru').node('SimpleRTS').node('model');
goldRequired=prefs.getInt('RequiredGold', 0);
woodRequired=prefs.getInt('RequiredWood', 0);

end
