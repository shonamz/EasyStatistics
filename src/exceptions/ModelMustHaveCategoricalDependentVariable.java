package exceptions;

public class ModelMustHaveCategoricalDependentVariable extends VariableException {
	private static final long serialVersionUID = 1L;
	String type;

	public ModelMustHaveCategoricalDependentVariable(String name,String type) {
		super(name);
		this.type = type;
	}
	
	@Override 
	public String getMessage() {
		return "Dependent variable must be binary in a "+type;
	}
}
