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
package edu.cwru.sepia.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.json.*;

import edu.cwru.sepia.environment.IDDistributer;
import edu.cwru.sepia.model.Template;
import edu.cwru.sepia.model.unit.UnitTemplate;
import edu.cwru.sepia.model.upgrade.UpgradeTemplate;

public final class TypeLoader {
	private TypeLoader(){}
	
	public static List<Template<?>> loadFromFile(String filename, int player, IDDistributer idsource) throws FileNotFoundException, JSONException {
		
		
		List<UpgradeTemplate> uptemplates = new ArrayList<UpgradeTemplate>();
		List<JSONObject> upobjs = new ArrayList<JSONObject>();
		List<UnitTemplate> untemplates = new ArrayList<UnitTemplate>();
		List<JSONObject> unobjs = new ArrayList<JSONObject>();
		StringBuilder sb = new StringBuilder();
		Scanner in = new Scanner(new File(filename));
		while(in.hasNextLine())
		{
			sb.append(in.nextLine());
			sb.append("\n");
		}
		in.close();
		JSONObject templateFile = new JSONObject(sb.toString());
		String[] keys = JSONObject.getNames(templateFile);
		for(String key : keys)
		{
			JSONObject template = templateFile.getJSONObject(key);
			String templateType = template.getString("TemplateType");
			if(templateType.equals("Unit"))
			{
				untemplates.add(handleUnit(template,player,idsource));
				unobjs.add(template);
			}
			else if (templateType.equals("Upgrade"))
			{
				uptemplates.add(handleUpgrade(template,player,idsource));
				upobjs.add(template);
			}
		}
		
		for (int i = 0; i<uptemplates.size();i++) {
			handleBothSecondPass(uptemplates.get(i), upobjs.get(i), untemplates, uptemplates);
			handleUpgradeSecondPass(uptemplates.get(i), upobjs.get(i), untemplates, uptemplates);
		}
		for (int i = 0; i<untemplates.size();i++) {
			handleBothSecondPass(untemplates.get(i), unobjs.get(i), untemplates, uptemplates);
			handleUnitSecondPass(untemplates.get(i), unobjs.get(i), untemplates, uptemplates);
		}
			
		List<Template<?>> templates = new ArrayList<Template<?>>();
		for (Template<?> t : uptemplates)
			templates.add(t);
		
		for (Template<?> t : untemplates)
			templates.add(t);
		return templates;
	}
	private static void handleUpgradeSecondPass(UpgradeTemplate toModify, JSONObject obj, List<UnitTemplate> unitTemplates, List<UpgradeTemplate> upgradeTemplates) throws JSONException {
		if(obj.has("Affects"))
		{
			JSONArray produces = obj.getJSONArray("Affects");
			for(int i = 0; i < produces.length(); i++)
			{
				String name = produces.getString(i);
				boolean found = false;
				Integer idFound=null;
				for (UnitTemplate t : unitTemplates) {
					if (name.equals(t.getName())) {
						found = true;
						idFound = t.ID;
						break;
					}
				}
				if (!found) {
					for (UnitTemplate t : unitTemplates) {
						if (name.equals(t.getName())) {
							found = true;
							idFound = t.ID;
							break;
						}
					}
				}
				if (found == true)
					toModify.addAffectedUnit(idFound);
			}
			
		}
		
	}
	private static void handleBothSecondPass(Template<?> toModify, JSONObject obj, List<UnitTemplate> unitTemplates, List<UpgradeTemplate> upgradeTemplates) throws JSONException {
		if(obj.has("BuildPrereq"))
		{
			JSONArray reqs = obj.getJSONArray("BuildPrereq");
			for(int i = 0; i < reqs.length(); i++) {
//				System.out.println(template.getName() + " requires building: " + reqs.getString(i));
				String name = reqs.getString(i);
				boolean found = false;
				Integer idFound=null;
				for (UnitTemplate t : unitTemplates) {
					if (name.equals(t.getName())) {
						found = true;
						idFound = t.ID;
						break;
					}
				}
				if (!found) {
					for (UnitTemplate t : unitTemplates) {
						if (name.equals(t.getName())) {
							found = true;
							idFound = t.ID;
							break;
						}
					}
				}
				if (found == true)
					toModify.addBuildPrerequisite(idFound);
			}
				
			
		}
		if(obj.has("UpgradePrereq"))
		{
			JSONArray reqs = obj.getJSONArray("UpgradePrereq");
			for(int i = 0; i < reqs.length(); i++) {
//				System.out.println(template.getName() + " requires building: " + reqs.getString(i));
				String name = reqs.getString(i);
				boolean found = false;
				Integer idFound=null;
				for (UnitTemplate t : unitTemplates) {
					if (name.equals(t.getName())) {
						found = true;
						idFound = t.ID;
						break;
					}
				}
				if (!found) {
					for (UnitTemplate t : unitTemplates) {
						if (name.equals(t.getName())) {
							found = true;
							idFound = t.ID;
							break;
						}
					}
				}
				if (found == true)
					toModify.addUpgradePrerequisite(idFound);
			}
		}
	}
	private static void handleUnitSecondPass(UnitTemplate toModify, JSONObject obj, List<UnitTemplate> unitTemplates, List<UpgradeTemplate> upgradeTemplates) throws JSONException {
		if(obj.has("Produces"))
		{
			JSONArray produces = obj.getJSONArray("Produces");
			for(int i = 0; i < produces.length(); i++)
			{
				String name = produces.getString(i);
				boolean found = false;
				Integer idFound=null;
				for (UnitTemplate t : unitTemplates) {
					if (name.equals(t.getName())) {
						found = true;
						idFound = t.ID;
						break;
					}
				}
				if (!found) {
					for (UnitTemplate t : unitTemplates) {
						if (name.equals(t.getName())) {
							found = true;
							idFound = t.ID;
							break;
						}
					}
				}
				if (found == true)
					toModify.addProductionItem(idFound);
			}
			
		}
		
	}
	private static UnitTemplate handleUnit(JSONObject obj, int player, IDDistributer idsource) throws JSONException {
		UnitTemplate template = new UnitTemplate(idsource.nextTemplateID());
		template.setName(obj.getString("Name"));
		if(obj.has("Mobile"))
			template.setCanMove(obj.getBoolean("Mobile"));
		else
			template.setCanMove(false);
		if(obj.has("Builder"))
			template.setCanBuild(obj.getBoolean("Builder"));
		else
			template.setCanBuild(false);
		if(obj.has("Gatherer"))
			template.setCanGather(obj.getBoolean("Gatherer"));
		else
			template.setCanGather(false);
		if (obj.has("HitPoints"))
			template.setBaseHealth(obj.getInt("HitPoints"));
		else
			template.setBaseHealth(1);
		if (obj.has("Armor"))
			template.setArmor(obj.getInt("Armor"));
		else
			template.setArmor(0);
		if (obj.has("Character"))
			template.setCharacter(obj.getString("Character").charAt(0));
		else
			template.setCharacter('?');
		if(obj.has("BasicAttack"))
		{
			template.setBasicAttack(obj.getInt("BasicAttack"));
		}
		else
			template.setBasicAttack(0);
		if(obj.has("Piercing"))
			template.setPiercingAttack(obj.getInt("Piercing"));
		else
			template.setPiercingAttack(0);
		if(obj.has("Range"))
			template.setRange(obj.getInt("Range"));
		else
			template.setRange(1);
		if (obj.has("SightRange"))
			template.setSightRange(obj.getInt("SightRange"));
		else
			template.setSightRange(0);
		if (obj.has("TimeCost"))
		{
			int timecost = obj.getInt("TimeCost");
			if (timecost < 1)
				throw new IllegalArgumentException("Time cost must be a positive integer");
			template.setTimeCost(timecost);
		}
		else
			template.setTimeCost(1);
		if(obj.has("FoodCost"))
			template.setFoodCost(obj.getInt("FoodCost"));
		else
			template.setFoodCost(0);
		if (obj.has("GoldCost"))
			template.setGoldCost(obj.getInt("GoldCost"));
		else
			template.setGoldCost(0);
		if (obj.has("WoodCost"))
			template.setWoodCost(obj.getInt("WoodCost"));
		else
			template.setWoodCost(0);
		if(obj.has("FoodGiven"))
			template.setFoodProvided(obj.getInt("FoodGiven"));
		else
			template.setFoodProvided(0);
		
		if(obj.has("AcceptsGold"))
			template.setCanAcceptGold(obj.getBoolean("AcceptsGold"));
		else
			template.setCanAcceptGold(false);
		if(obj.has("AcceptsWood"))
			template.setCanAcceptWood(obj.getBoolean("AcceptsWood"));
		else
			template.setCanAcceptWood(false);
		
		if(obj.has("WoodPerTrip"))
			template.setWoodGatherRate(obj.getInt("WoodPerTrip"));
		else
			template.setWoodGatherRate(0);
		if(obj.has("GoldPerTrip"))
			template.setGoldGatherRate(obj.getInt("GoldPerTrip"));
		else
			template.setGoldGatherRate(0);
		
		
		
		
		//Various duration fields:
		if(obj.has("DurationMove"))
		{
			int duration = obj.getInt("DurationMove");
			//negative durations are meanless
			//zero duration is meaningless so long as a unit can do only one action per turn
			if (duration < 1)
			{
				throw new IllegalArgumentException("DurationMove must be positive");
			}
			template.setDurationMove(duration);
		}
		else
		{
			template.setDurationMove(1);
		}
		if(obj.has("DurationAttack"))
		{
			int duration = obj.getInt("DurationAttack");
			//negative durations are meanless
			//zero duration is meaningless so long as a unit can do only one action per turn
			if (duration < 1)
			{
				throw new IllegalArgumentException("DurationAttack must be positive");
			}
			template.setDurationAttack(duration);
		}
		else
		{
			template.setDurationAttack(1);
		}
		if(obj.has("DurationDeposit"))
		{
			int duration = obj.getInt("DurationDeposit");
			//negative durations are meanless
			//zero duration is meaningless so long as a unit can do only one action per turn
			if (duration < 1)
			{
				throw new IllegalArgumentException("DurationDeposit must be positive");
			}
			template.setDurationDeposit(duration);
		}
		else
		{
			template.setDurationDeposit(1);
		}
		if(obj.has("DurationGatherGold"))
		{
			int duration = obj.getInt("DurationGatherGold");
			//negative durations are meanless
			//zero duration is meaningless so long as a unit can do only one action per turn
			if (duration < 1)
			{
				throw new IllegalArgumentException("DurationGatherGold must be positive");
			}
			template.setDurationGatherGold(duration);
		}
		else
		{
			template.setDurationGatherGold(1);
		}
		if(obj.has("DurationGatherWood"))
		{
			int duration = obj.getInt("DurationGatherWood");
			//negative durations are meanless
			//zero duration is meaningless so long as a unit can do only one action per turn
			if (duration < 1)
			{
				throw new IllegalArgumentException("DurationGatherWood must be positive");
			}
			template.setDurationGatherWood(duration);
		}
		else
		{
			template.setDurationGatherWood(1);
		}
		
		template.setPlayer(player);
		return template;
	}
	private static UpgradeTemplate handleUpgrade(JSONObject obj, int player, IDDistributer idsource) throws JSONException {
		
		int piercingattackchange = 0;
		int basicattackchange = 0;
		int armorchange = 0;
		int rangechange = 0;
		int healthchange = 0;
		if (obj.has("PiercingAttackIncrease"))
		{
			piercingattackchange = obj.getInt("PiercingAttackIncrease");
		}
		if (obj.has("BasicAttackIncrease"))
		{
			basicattackchange = obj.getInt("BasicAttackIncrease");
		}
		if (obj.has("ArmorIncrease"))
		{
			armorchange = obj.getInt("ArmorIncrease");
		}
		if (obj.has("HealthIncrease"))
		{
			healthchange = obj.getInt("HealthIncrease");
		}
		if (obj.has("RangeIncrease"))
		{
			rangechange = obj.getInt("RangeIncrease");
		}
		
		UpgradeTemplate template = new UpgradeTemplate(idsource.nextTemplateID());
		template.setPiercingAttackChange(piercingattackchange);
		template.setBasicAttackChange(basicattackchange);
		template.setArmorChange(armorchange);
		template.setRangeChange(rangechange);
		template.setHealthChange(healthchange);
		template.setName(obj.getString("Name"));
		if (obj.has("TimeCost"))
		{
			int timecost = obj.getInt("TimeCost");
			if (timecost < 1)
				throw new IllegalArgumentException("Time cost must be a positive integer");
			template.setTimeCost(timecost);
		}
		else
			template.setTimeCost(1);
		if (obj.has("GoldCost"))
			template.setGoldCost(obj.getInt("GoldCost"));
		else
			template.setGoldCost(0);
		if (obj.has("WoodCost"))
			template.setWoodCost(obj.getInt("WoodCost"));
		else
			template.setWoodCost(0);
		template.setPlayer(player);
		return template;
	}
}
