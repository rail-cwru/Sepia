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
import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
/**
 * Manages a list of configurable properties and allows for saving to/loading from a file.
 * @author Tim
 *
 */
public class Configuration {
	
	private static final String USELESS_TEXT="#text";
	private static final String DEPRECATED_PREFIX="edu.cwru.sepia.";
	private HashMap<String,Object> settings;
	
	public Configuration() {
		settings = new HashMap<String,Object>();
	}
	
	public Object get(String key) {
		return settings.get(key);
	}
	
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return 
	 * @return
	 */
	public <T> T get(String key, T fallback) {
		T property = (T)settings.get(key);
		return property != null  ?  property : fallback;
	}
	
	public String getString(String key) {
		return (String)settings.get(key);
	}
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public String getString(String key, String fallback) {
		String property = (String)settings.get(key);
		return property != null ? property: fallback;
	}
	
	/**
	 * Get a boolean property
	 * @param key
	 * @return
	 */
	public Boolean getBoolean(String key) {
		Object o = settings.get(key);
		if (o == null)
			return null;
		if(o instanceof Boolean)
			return (Boolean)o;
		else if(o.toString().equalsIgnoreCase("TRUE"))
			return Boolean.TRUE;
		else if(o.toString().equalsIgnoreCase("FALSE"))
			return Boolean.FALSE;
		else
			return null;
	}
	
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Boolean getBoolean(String key, boolean fallback) {
		Boolean property = getBoolean(key);
		return property != null  ?  property : fallback;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInt(String key) {
		Object o = settings.get(key);
		if (o == null)
			return null;
		if(o instanceof Integer)
			return (Integer)o;
		else
			return Integer.parseInt(o.toString());
	}
	/**
	 * Get an integer property
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Integer getInt(String key, int fallback) {
		Integer property = null;
		try
		{
			property = getInt(key);
		} catch(NumberFormatException ex) {}
		return property != null  ?  property : fallback;
	}
	
	public Double getDouble(String key) {
		Object o = settings.get(key);
		if (o == null)
			return null;
		if(o instanceof Double)
			return (Double)o;
		else
			return Double.parseDouble(o.toString());
	}
	/**
	 * 
	 * @param key The name of the property to get.
	 * @param fallback A value to use if no such property is set.
	 * @return
	 */
	public Double getDouble(String key, Double fallback) {
		Double property = null;
		try
		{
			property = getDouble(key);
		} catch(NumberFormatException ex) {}
		return property != null  ?  property : fallback;
	}
	public void put(String key, String value) {
		settings.put(key, value);
	}
	
	public void put(String key, boolean value) {
		settings.put(key, value);
	}
	
	public void put(String key, int value) {
		settings.put(key, value);
	}
	
	public void put(String key, double value) {
		settings.put(key, value);
	}
	
	public <T> void put(String key, T value) {
		settings.put(key, value);
	}
	
	public Set<String> getKeys() {
		return settings.keySet();
	}
	
	public boolean containsKey(String key) {
		return settings.containsKey(key);
	}
	
	@Override
	public String toString() {
		return settings.toString();
	}

	/**
	 * Treat a file as a java preferences file and load it into a configuration, without the unsafe use of java's preferences.
	 * @param configFilePath
	 * @return a configuration parsed from the file
	 */
	public static Configuration loadPreferenceFormatConfiguration(String configFilePath) {
		File config = new File(configFilePath);
		String configAbsPath= config.getAbsolutePath();
		try {
			Configuration configuration = new Configuration();
			DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
			Document xmlDoc = docBuild.parse(config);
			
			NodeList rootChildren = xmlDoc.getElementsByTagName("preferences");
			int nRootChildren = rootChildren.getLength();
			if (nRootChildren != 1) {
				throw new IllegalArgumentException("Expected 1 root node called preferences, but there were "+ nRootChildren);
			}
			else {
				Node preferenceNode = rootChildren.item(0);
				NodeList children = preferenceNode.getChildNodes();
				for (Node nodeCalledRoot : getNonTextIterator(children)) {
					if (!"root".equals(nodeCalledRoot.getNodeName())) {
						throw new IllegalArgumentException("Expected child of preferences to be root, but it was "+nodeCalledRoot.getNodeName());
					}
					else
					{
						for (Node node : getNonTextIterator(nodeCalledRoot.getChildNodes())) {
							addToPreferenceFormatConfiguration(node, configuration, "");
						}
					}
						
				}
				return configuration;
			}
			
		} catch (ParserConfigurationException e) {
			throw new IllegalArgumentException("Unable to parse configuration file: couldn't build parser"+configAbsPath,e);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Unable to parse configuration file: couldn't parser file"+configAbsPath,e);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to parse configuration file: couldn't read file"+configAbsPath,e);
		}
		catch (RuntimeException e) {
			throw new IllegalArgumentException("Unable to parse configuration file: run time exception"+configAbsPath,e);
		}
	}
	/**
	 * 
	 * @param node
	 * @param configuration
	 * @param levelName the name of the current level of nodes, hierarchy-wise
	 */
	private static void addToPreferenceFormatConfiguration(Node node, Configuration configuration, final String levelName) {
		
		String nodeName = node.getNodeName();
		if ("node".equals(nodeName)) {
			NodeList children = node.getChildNodes();
			Node nameAttrib = node.getAttributes().getNamedItem("name");
			if (nameAttrib == null) {
				throw new IllegalArgumentException("All <node> must have a name attribute");
			}
			String newLevelName = levelName + nameAttrib.getNodeValue() + ".";
			for (Node child : getNonTextIterator(children) ) { 
				addToPreferenceFormatConfiguration(child, configuration, newLevelName);
			}
		}
		else if ("map".equals(nodeName)) {
			NodeList children = node.getChildNodes();
			for (Node child : getNonTextIterator(children) ) {
				if (!"entry".equals(child.getNodeName())) {
					throw new IllegalArgumentException("All children of map must be entry");
				}
				else {
					Node key = child.getAttributes().getNamedItem("key");
					Node value = child.getAttributes().getNamedItem("value");
					if (key == null || value == null) {
						throw new IllegalArgumentException("All entries must have key and value attributes");
					}
					else {
						String newConfigName = levelName+key.getNodeValue();
						if (newConfigName.startsWith(DEPRECATED_PREFIX)) {
							newConfigName = newConfigName.replaceFirst(DEPRECATED_PREFIX, "");
						}
						configuration.put(newConfigName, value.getNodeValue());
					}
					
				}
			}
		}
		else {
			throw new RuntimeException("All non-root nodes must be map or node (or entry, if the parent is map), not "+nodeName);
		}
	}
	
	
	/**
	 * Helper class to make nodes iterable to allow for cleaner loops elsewhere.
	 * <br>Assumes that the nodelists are unchanging and leaves remove as nonfunctional;
	 * @author The Condor
	 *
	 */
	private static Iterable<Node> getNonTextIterator(final NodeList nodeList) {
		List<Node> list = new LinkedList<Node>();
		int nChildren = nodeList.getLength();
		for (int i = 0; i<nChildren; i++) {
			Node node = nodeList.item(i);
			if (!USELESS_TEXT.equals(node.getNodeName()))
				list.add(node);
		}
		return list;
	}
}
