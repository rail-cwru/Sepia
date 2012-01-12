% Note that this works under Windoes 7.
% But doesn't work properly under Max OS X and Linux.

clear java  % this command doesn't work properly under Mac OS X, but works under Windows 7. But doesn't matter a lot.

% use this line when you use jar directly
javaaddpath({[fileparts(pwd),'/SimpleRTSv3.1.jar'], [fileparts(pwd),'/lib/matlabcontrol-4.0.0.jar']}); 

% use this line under Eclipse project
%javaaddpath({[fileparts(pwd),'/bin'], [fileparts(pwd),'/lib/matlabcontrol-4.0.0.jar']}); 

%javaclasspath % print out current java class path

import edu.cwru.SimpleRTS.*;

arg = '--config ../data/midasConfig.xml ../data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0 --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam false --agentparam true';
%arg = '--config ../data/midasConfig.xml ../data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0';
args = regexpi(arg, ' +', 'split');
Main.main(args);

