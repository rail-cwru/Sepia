package edu.cwru.SimpleRTS.util;

import java.util.List;
import java.util.Map;

import edu.cwru.SimpleRTS.action.Action;

/**
 * Some utility methods to use DeepEquals on Collections, as well as null-checking on both.
 * @author The Condor
 *
 */
public class DeepEquatableUtil {
	/**
	 * A method to compare the deep equality of two Lists of Lists. <BR>
	 * Suffixed by ListList due to type erasure, which makes generics not overload intuitively.
	 * @param obj1
	 * @param obj2
	 * @return True if both arguments are null or if both are non-null, of the same size, and objects with the same indices are deepEquals to eachother. False otherwise.
	 */
	public static boolean deepEqualsListList(List<? extends List<? extends DeepEquatable>> obj1, List<? extends List<? extends DeepEquatable>> obj2) {
			boolean thisnull = obj1 == null;
			boolean othernull = obj2 == null;
			if ((thisnull == othernull)==false)
			{
				return false;
			}
			//if both aren't null, need to check deeper
			if (!thisnull && !othernull)
			{
				if (obj1.size() != obj2.size())
					return false;
				for (int i = 0; i<obj1.size();i++)
				{
					List<? extends DeepEquatable> thisinner=obj1.get(i);
					List<? extends DeepEquatable> otherinner=obj2.get(i);
					if (!deepEqualsList(thisinner,otherinner))
						return false;
				}
			}
			return true;
	}
	/**
	 * A method to compare the deep equality of two Lists. <BR>
	 * Suffixed by List due to type erasure, which makes generics not overload intuitively.
	 * @param obj1
	 * @param obj2
	 * @return True if both arguments are null or if both are non-null, of the same size, and objects with the same indices are deepEquals to eachother.  False otherwise.
	 */
	public static boolean deepEqualsList(List<? extends DeepEquatable> obj1, List<? extends DeepEquatable> obj2) {
		boolean obj1null = obj1 == null;
		boolean obj2null = obj2 == null;
		if ((obj1null == obj2null)==false)
		{
			return false;
		}
		//if both aren't null, need to check deeper
		if (!obj1null && !obj2null)
		{
			if (obj1.size() != obj2.size())
				return false;
			for (int i = 0; i<obj1.size();i++)
			{
				DeepEquatable obj1element=obj1.get(i);
				DeepEquatable obj2element=obj2.get(i);
				if (!deepEquals(obj1element,obj2element))
					return false;
			}
		}
		return true;
	}
	/**
	 * A method to compare the deep equality of two DeepEquatable objects.
	 * @param obj1
	 * @param obj2
	 * @return True if both arguments are null of if both are non-null and deepEquals to eachother.  False otherwise.
	 */
	public static boolean deepEquals(DeepEquatable obj1, DeepEquatable obj2) {
		boolean obj1null = obj1 == null;
		boolean obj2null = obj2 == null;
		if ((obj1null == obj2null)==false)
		{
			return false;
		}
		//if both aren't null, need to check deeper
		if (!obj1null && !obj2null)
		{
			if (!obj1.deepEquals(obj2))
				return false;
		}
		return true;
	}
	/**
	 * A method to compare the deep equality of two Maps. <BR>
	 * Suffixed by Map for consistancy with others and extensibility.
	 * @param obj1
	 * @param obj2
	 * @return True if both arguments are null or if both are non-null, of the same size, and objects with the same indices are deepEquals to eachother.  False otherwise.
	 */
	public static <T> boolean deepEqualsMap(Map<T,? extends DeepEquatable> obj1, Map<T,? extends DeepEquatable> obj2)
	{
		boolean obj1null = obj1 == null;
		boolean obj2null = obj2 == null;
		if ((obj1null == obj2null)==false)
		{
			return false;
		}
		//if both aren't null, need to check deeper
		if (!obj1null && !obj2null)
		{
			if (obj1.size() != obj2.size())
				return false;
			for (T index : obj1.keySet())
			{
				if (!deepEquals(obj1.get(index),obj2.get(index)))
					return false;
			}
		}
		
		return true;
	}
}
