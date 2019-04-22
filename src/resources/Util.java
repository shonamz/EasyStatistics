package resources;

import java.util.Hashtable;

public abstract class Util {
	// Helper methods

	// convert Double[] to double[]
	public static double[] convertToPrimitive(Double[] values) {
		double[] prim_values = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i] != null)
				prim_values[i] = values[i].doubleValue();
		}
		return prim_values;
	}
	
	// This method parses a string into a hash
	//  key1=val1, key2=val2 => {"key1"=>"val1","key2"=>"val2"}
	public static Hashtable<String,String> stringToHash(String text) {
		Hashtable<String,String> hash = new Hashtable<String,String>();
		
		// iterate over param groups
		for(String group: text.split(",")) {
			String[] groupArray = group.split("=");
			if(groupArray.length >= 2) {
				hash.put(groupArray[0].trim(), groupArray[1].trim());
			} else if (groupArray.length > 0) {
				hash.put(groupArray[0].trim(), "");
			}
		}
		
		return hash;
	}
}


