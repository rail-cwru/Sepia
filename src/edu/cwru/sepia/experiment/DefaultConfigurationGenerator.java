/**
 *  Strategy Engine for Programming Intelligent Agents (SEPIA)
    Copyright (C) 2012 Case Western Reserve University

    This file is part of SEPIA.

    SEPIA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SEPIA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SEPIA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.cwru.sepia.experiment;


/**
 * A utility class with a factory method to make a default configuration.
 * <br>It is highly recommended that a configuration be custom-made for the particular instance of {@link Runner}.
 * 
 *
 */
public class DefaultConfigurationGenerator {
	/**
	 * Makes a default configuration (conquest with one episode with a large (2^16-1) time limit)
	 * <br>It is highly recommended that a configuration be custom-made for the particular instance of {@link Runner}.
	 * @return
	 */
	public static Configuration getDefaultConfiguration(){
		Configuration configuration = new Configuration();
		configuration.put("experiment.NumEpisodes", 1);
		configuration.put("environment.model.Conquest", true);
		configuration.put("environment.model.Midas", false);
		configuration.put("environment.model.ManifestDestiny", false);
		configuration.put("environment.model.TimeLimit", 65535);
		return configuration;
	}
}
