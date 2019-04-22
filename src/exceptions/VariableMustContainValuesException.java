package exceptions;

public class VariableMustContainValuesException extends VariableException {
	private static final long serialVersionUID = 1L;

	public VariableMustContainValuesException(String name) {
		super(name);
	}
	
	@Override 
	public String getMessage() {
		return "Variable "+name+" has no values";
	}

}
