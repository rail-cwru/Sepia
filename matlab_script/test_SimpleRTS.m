clear java
javaaddpath({[fileparts(pwd),'\bin'], [fileparts(pwd),'\lib\matlabcontrol-4.0.0.jar'], [fileparts(pwd),'\lib\guava-r09']});
%javaclasspath
import edu.cwru.SimpleRTS.*;


%arg = '--config data/defaultConfig.xml data/footmen8v8.map --agent edu.cwru.SimpleRTS.agent.QCombatAgent 0 --agent edu.cwru.SimpleRTS.agent.CombatAgent 1 --agentparam 0 --agentparam true --agentparam false';
%arg = '--config data/defaultConfig.xml data/midas5.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0 --agent edu.cwru.SimpleRTS.agent.CombatAgent 1 --agentparam 0 --agentparam true --agentparam false';
arg = '--config data/midasConfig.xml data/midas5.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0';
args = regexpi(arg, ' +', 'split');
Main.main(args);