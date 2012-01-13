package edu.cwru.SimpleRTS.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.json.*;

import edu.cwru.SimpleRTS.environment.IDDistributer;
import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.UnitTemplate;
import edu.cwru.SimpleRTS.model.upgrade.UpgradeTemplate;

public final class TypeLoader {
	private TypeLoader(){}
	
	public static List<Template> loadFromFile(String filename, int player, IDDistributer idsource) throws FileNotFoundException, JSONException {
		List<Template> templates = new ArrayList<Template>();
//		System.err.println("Getting upgrade templates");
		List<UpgradeTemplate> uptemplates  = loadUpgradesFromFile(filename, player,idsource);
		for (Template t : uptemplates)
			templates.add(t);
//		System.err.println("Getting unit templates");
		List<UnitTemplate> untemplates  = loadUnitsFromFile(filename, player,idsource);
		for (Template t : untemplates)
			templates.add(t);
//		System.err.println("Putting templates into other templates");
		for (Template t : templates) {
			
//			System.out.println(t + " " + t);
			t.namesToIds(untemplates, uptemplates);
		}
		return templates;
	}
	public static List<UpgradeTemplate> loadUpgradesFromFile(String filename, int player, IDDistributer idsource) throws FileNotFoundException, JSONException {
//		System.out.println("Loading upgrade");
		List<UpgradeTemplate> list = new ArrayList<UpgradeTemplate>();
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
			if(templateType.equals("Upgrade"))
				list.add(handleUpgrade(template,key,player,idsource));
		}
		return list;
	}
	public static List<UnitTemplate> loadUnitsFromFile(String filename, int player, IDDistributer idsource) throws FileNotFoundException, JSONException {
		List<UnitTemplate> list = new ArrayList<UnitTemplate>();
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
				list.add(handleUnit(template,key,player,idsource));
		}
		return list;
	}
	private static UnitTemplate handleUnit(JSONObject obj, String name, int player, IDDistributer idsource) throws JSONException {
		UnitTemplate template = new UnitTemplate(idsource.nextTemplateID());
		template.setName(obj.getString("Name"));
		if(obj.has("Mobile"))
			template.setCanMove(obj.getBoolean("Mobile"));
		if(obj.has("Builder"))
			template.setCanBuild(obj.getBoolean("Builder"));
		if(obj.has("Gatherer"))
			template.setCanGather(obj.getBoolean("Gatherer"));
		template.setBaseHealth(obj.getInt("HitPoints"));
		template.setArmor(obj.getInt("Armor"));
		template.setCharacter(obj.getString("Character").charAt(0));
		if(obj.has("BasicAttack"))
			template.setBasicAttack(obj.getInt("BasicAttack"));
		if(obj.has("Piercing"))
			template.setPiercingAttack(obj.getInt("Piercing"));
		if(obj.has("Range"))
			template.setRange(obj.getInt("Range"));
		template.setSightRange(obj.getInt("SightRange"));
		template.setTimeCost(obj.getInt("TimeCost"));
		if(obj.has("FoodCost"))
			template.setFoodCost(obj.getInt("FoodCost"));
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
		
		
		template.setGoldCost(obj.getInt("GoldCost"));
		template.setWoodCost(obj.getInt("WoodCost"));
		
		template.setPlayer(player);
		if(obj.has("Produces"))
		{
			JSONArray produces = obj.getJSONArray("Produces");
			for(int i = 0; i < produces.length(); i++)
			{
				template.addProductionItem(produces.getString(i));
			}
			
		}
		if(obj.has("BuildPrereq"))
		{
			JSONArray reqs = obj.getJSONArray("BuildPrereq");
			for(int i = 0; i < reqs.length(); i++) {
				template.addBuildPrereqItem(reqs.getString(i));
//				System.out.println(template.getName() + " requires building: " + reqs.getString(i));
			}
				
			
		}
		if(obj.has("UpgradePrereq"))
		{
			JSONArray reqs = obj.getJSONArray("UpgradePrereq");
			for(int i = 0; i < reqs.length(); i++)
			{
//				System.out.println(template.getName() + " requires upgrade: " + reqs.getString(i));
				template.addUpgradePrereqItem(reqs.getString(i));		
			}
		}
		template.setUnitName(name);
		return template;
	}
	private static UpgradeTemplate handleUpgrade(JSONObject obj, String name, int player, IDDistributer idsource) throws JSONException {
		String[] affectslist = null;
		if(obj.has("Affects"))
		{
			JSONArray affects = obj.getJSONArray("Affects");
			affectslist=new String[affects.length()];
			for(int i = 0; i < affects.length(); i++)
				affectslist[i]=affects.getString(i);
		}
		int attackchange = 0;
		int defensechange = 0;
		if (obj.has("DamageIncrease"))
		{
			attackchange = obj.getInt("DamageIncrease");
		}
		if (obj.has("ArmorIncrease"))
		{
			defensechange = obj.getInt("ArmorIncrease");
		}
		
		UpgradeTemplate template = new UpgradeTemplate(idsource.nextTemplateID(),attackchange, defensechange, affectslist);
		template.setName(obj.getString("Name"));
		template.setTimeCost(obj.getInt("TimeCost"));
		template.setGoldCost(obj.getInt("GoldCost"));
		template.setWoodCost(obj.getInt("WoodCost"));
		template.setPlayer(player);
		if(obj.has("BuildPrereq"))
		{
			JSONArray reqs = obj.getJSONArray("BuildPrereq");
			for(int i = 0; i < reqs.length(); i++) {
				template.addBuildPrereqItem(reqs.getString(i));
//				System.out.println(template.getName() + " requires building: " + reqs.getString(i));
			}
				
			
		}
		if(obj.has("UpgradePrereq"))
		{
			JSONArray reqs = obj.getJSONArray("UpgradePrereq");
			for(int i = 0; i < reqs.length(); i++)
			{
//				System.out.println(template.getName() + " requires upgrade: " + reqs.getString(i));
				template.addUpgradePrereqItem(reqs.getString(i));		
			}
		}
		return template;
	}
}
