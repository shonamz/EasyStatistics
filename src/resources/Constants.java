package resources;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class Constants {
	public static final ArrayList<Character> INVALID_CHARS = new ArrayList<Character>(Arrays.asList(',','|','='));
	public static final ArrayList<String> MODEL_TYPES = new ArrayList<String>(Arrays.asList("linear","lm","linmod","quadratic","quad","quadm","quadlm","logm","log","semilog","logarithmic","loglm","loglin","loglinear"));
	public static final ArrayList<String> LINEAR_MODELS = new ArrayList<String>(Arrays.asList("linear","lm","linmod"));
	public static final ArrayList<String> QUADRATIC_MODELS = new ArrayList<String>(Arrays.asList("quadratic","quad","quadm","quadlm"));
	public static final ArrayList<String> LOG_MODELS = new ArrayList<String>(Arrays.asList("log","logm","loglog","logarithmic"));
	public static final ArrayList<String> LOGLINEAR_MODELS = new ArrayList<String>(Arrays.asList("semilog","poisson","loglinear","loglm","loglin"));
	public static final Color NULL_VALUES_COLOR = Color.red;
	public static final Color NO_VALUES_COLOR = Color.blue;
	public static final Color INCONSISTENT_MODEL_COLOR = Color.green;
}
