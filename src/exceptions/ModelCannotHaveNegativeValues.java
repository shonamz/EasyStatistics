package exceptions;

public class ModelCannotHaveNegativeValues extends VariableException {
	private static final long serialVersionUID = 1L;
	String type;

	public ModelCannotHaveNegativeValues(String name, String type) {
		super(name);
		this.type = type;
	}
	
	@Override 
	public String getMessage() {
		return "Dependent variable cannot have negative values in a "+type;
	}
}
