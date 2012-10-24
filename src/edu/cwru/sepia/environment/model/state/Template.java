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
package edu.cwru.sepia.environment.model.state;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.cwru.sepia.util.DeepEquatable;

/**
 * Signifies that an implementing class provides generic details about a specific object.
 * Also provides a means for creating factory methods for specific kinds of game objects.
 * @author Tim
 *
 * @param <T>
 */
public abstract class Template<T> implements Serializable, DeepEquatable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int timeCost;
	protected int goldCost;
	protected int woodCost;
	protected int foodCost;
	protected int player;
	protected Set<Integer> buildPrerequisites;
	protected Set<Integer> upgradePrerequisites;
	protected String name;
	public final int ID;
	/**
	 * A factory method that produces copies of a "default" object
	 * @return
	 */
	public abstract T produceInstance(IDDistributer idsource);
	public Template(int ID)
	{
		this.ID = ID;
		buildPrerequisites = new HashSet<Integer>();
		upgradePrerequisites = new HashSet<Integer>();
	}
	/**
	 * @param view
	 */
	public Template(TemplateView view) {
		this.ID = view.ID;
		for (Integer prereqUnitId : view.buildPrerequisites)
			addBuildPrerequisite(prereqUnitId);
		for (Integer prereqUpgradeId : view.upgradePrerequisites)
			addUpgradePrerequisite(prereqUpgradeId);
		setFoodCost(view.foodCost);
		setGoldCost(view.goldCost);
		setWoodCost(view.woodCost);
		setTimeCost(view.timeCost);
		setName(view.name);
		setPlayer(view.player);
	}
	public int getTimeCost() {
		return timeCost;
	}
	public void setTimeCost(int timeCost) {
		this.timeCost = timeCost;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public int getGoldCost() {
		return goldCost;
	}
	public void setGoldCost(int goldCost) {
		this.goldCost = goldCost;
	}
	public int getWoodCost() {
		return woodCost;
	}
	public void setWoodCost(int woodCost) {
		this.woodCost = woodCost;
	}
	public int getFoodCost() {
		return foodCost;
	}
	public void setFoodCost(int foodCost) {
		this.foodCost = foodCost;
	}
	public void setPlayer(int playerid) {
		this.player = playerid;
	}
	public int getPlayer() {
		return this.player;
	}
	
	/**
	 * Get the set of template ids of buildings (or units in general) that are required before this template can be made.
	 * <br>This list is mutable, changing it will alter the things needed to make this template.
	 * @return A set of template ids for prerequisite buildings/units
	 */
	public Set<Integer> getBuildPrerequisites() {
		return buildPrerequisites;
	}
	/**
	 * Get the set of template ids of upgrades that must be researched before this template can be made.
	 * <br>This list is mutable, changing it will alter the things needed to make this template.
	 * @return A set of template ids for prerequisite upgrades.
	 */
	public Set<Integer> getUpgradePrerequisites() {
		return upgradePrerequisites;
	}
	@Override
	public int hashCode() {
		return ID;
	}
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Template))
			return false;
		return ((Template<?>)o).ID == ID;
	}
	public abstract TemplateView getView();
	public abstract void deprecateOldView();
	public static class TemplateView implements Serializable{
		private static final long serialVersionUID = 7272928444209753621l;
		private final int timeCost;
		private final int goldCost;
		private final int woodCost;
		private final int foodCost;
		private final int ID;
		private final int player;
		private final String name;
		private final Set<Integer> buildPrerequisites;
		private final Set<Integer> upgradePrerequisites;
		public TemplateView(Template<?> template){
			timeCost = template.getTimeCost();
			goldCost = template.getGoldCost();
			woodCost = template.getWoodCost();
			foodCost = template.getFoodCost();
			ID = template.ID;
			player = template.getPlayer();
			name = template.getName();
			Set<Integer> tbuild = new HashSet<Integer>();
			for (Integer i : template.buildPrerequisites) {
				tbuild.add(i);
			}
			buildPrerequisites = Collections.unmodifiableSet(tbuild);
			Set<Integer> tupgrade = new HashSet<Integer>();
			for (Integer i : template.upgradePrerequisites) {
				tupgrade.add(i);
			}
			upgradePrerequisites = Collections.unmodifiableSet(tupgrade);
		}
		/**
		 * Get a(n unmodifiable) set of buildings or other units that must be built before a unit can be made with this template
		 * @return The ids of the templates of the required buildings/units
		 */
		public Set<Integer> getBuildPrerequisites() {
			return buildPrerequisites;
		}
		/**
		 * Get a(n unmodifiable) set of upgrades 
		 * @return The ids of the templates of the required upgrades
		 */
		public Set<Integer> getUpgradePrerequisites() {
			return upgradePrerequisites;
		}
		public int getTimeCost() {
			return timeCost;
		}
		public int getGoldCost() {
			return goldCost;
		}
		public int getWoodCost() {
			return woodCost;
		}
		public int getFoodCost() {
			return foodCost;
		}
		public int getID() {
			return ID;
		}
		public int getPlayer() {
			return player;
		}
		public String getName()
		{
			return name;
		}
	}
	/**
	 * Add an upgrade prerequisite (An upgrade that must be done before this template can be made)
	 * @param templateID
	 */
	public void addUpgradePrerequisite(Integer templateID) {
		upgradePrerequisites.add(templateID);
	}
	/**
	 * Add a building prerequisite (A unit that must be built before this template can be made)
	 * @param templateID
	 */
	public void addBuildPrerequisite(Integer templateID) {
		buildPrerequisites.add(templateID);
	}
}
