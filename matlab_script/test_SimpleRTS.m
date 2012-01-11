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

arg = '--config ../data/midasConfig.xml ../data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0 --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam false --agentparam true';
%arg = '--config ../data/midasConfig.xml ../data/rc_3m5t.map --agent edu.cwru.SimpleRTS.agent.MatlabAgent 0';
args = regexpi(arg, ' +', 'split');
Main.main(args);

