package resources;

public class Config {
	private static boolean isModelDimensionPolicyStrict = true;
	
	public static void setModelDimensionPolicyStrict(boolean isStrict) {
		isModelDimensionPolicyStrict = isStrict;
	}
	
	public static boolean isModelDimensionPolicyStrict() {
		return isModelDimensionPolicyStrict;
	}
	
	private static boolean isNullValuePolicyStrict = false;
	
	public static void setNullValuePolicyStrict(boolean isStrict) {
		isNullValuePolicyStrict = isStrict;
	}
	
	public static boolean isNullValuePolicyStrict() {
		return isNullValuePolicyStrict;
	}
}
