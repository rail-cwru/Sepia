package edu.cwru.sepia.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Scanner;

public final class KeyValueConfigurationUtil {

	private KeyValueConfigurationUtil() {}
	
	public static Configuration load(String filename) throws IOException, ParseException {
		Configuration configuration = new Configuration();
		
		int i = 0;
		Scanner in = null;
		try
		{
			in = new Scanner(new File(filename));
			while(in.hasNextLine())
			{
				String[] parts = in.nextLine().split("=", 2);
				if(parts.length < 2)
					throw new ParseException("Line " + i + " of configuration file " + filename + " is invalid", i);
				try
				{
					int intVal = Integer.parseInt(parts[1]);
					configuration.put(parts[0], intVal);
				}
				catch(NumberFormatException ex)
				{
					try
					{
						double doubleVal = Double.parseDouble(parts[1]);
						configuration.put(parts[0], doubleVal);
					}
					catch(NumberFormatException ex2)
					{
						if("true".equalsIgnoreCase(parts[1]))
							configuration.put(parts[0], true);
						else if("false".equalsIgnoreCase(parts[1]))
							configuration.put(parts[0], false);
						else
							configuration.put(parts[0], parts[1]);
					}				
				}
				i++;
			}
		}
		finally
		{
			if(in != null)
				in.close();
		}
		return configuration;
	}
	
	public static void save(Configuration configuration, String filename) throws IOException {
		PrintStream out = null;
		try
		{
			out = new PrintStream(new File(filename));
			save(configuration, out);
		}
		finally
		{
			if(out != null)
				out.close();
		}
	}
	
	public static void save(Configuration configuration, PrintStream out) {
		for(String key : configuration.getKeys())
		{
			out.println(key + "=" + configuration.get(key));
		}
	}
}
