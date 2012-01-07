% This doesn't work in Mac OS X with Matlab R2011b.
% Seems like in Mac OS X, matlab R2011b is using its default Java, which
% doesn't seems parsing the *Config.xml file correctly.

% However, this does work under Windoes 7. 
% -- Feng, 2012.01.06

clear java  % doesn't work properly under Mac OS X, but works under Windows 7.
javaaddpath({[fileparts(pwd),'/bin'], [fileparts(pwd),'/lib/matlabcontrol-4.0.0.jar'], [fileparts(pwd),'/lib/guava-r09']});
%javaclasspath
import edu.cwru.SimpleRTS.*;

%cd(pwd);
%fileattrib('data/midasConfig.xml');

%import java.util.prefs.Preferences;
%import java.io.FileInputStream;
%Preferences.importPreferences(FileInputStream('data/midasConfig.xml'));

%arg = '--config data/defaultConfig.xml data/footmen8v8.map --agent edu.cwru.SimpleRTS.agent.QCombatAgent 0 --agent edu.cwru.SimpleRTS.agent.CombatAgent 1 --agentparam 0 --agentparam true --agentparam false';
%arg = '--config data/defaultConfig.xml data/midas5.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0 --agent edu.cwru.SimpleRTS.agent.CombatAgent 1 --agentparam 0 --agentparam true --agentparam false';
arg = '--config data/midasConfig.xml data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0';
args = regexpi(arg, ' +', 'split');
Main.main(args);

