package edu.cwru.SimpleRTS;

import java.io.FileNotFoundException;
import java.util.List;

import org.json.JSONException;

import edu.cwru.SimpleRTS.model.Template;
import edu.cwru.SimpleRTS.model.unit.Unit;
import edu.cwru.SimpleRTS.util.UnitTypeLoader;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, JSONException {
		List<Template> templates = UnitTypeLoader.loadFromFile("data/unit_templates");
		for(Template t : templates)
		{
			System.out.println(t.produceInstance());
		}
	}
}
