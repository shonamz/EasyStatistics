package exceptions;

public class VariableContainsNonNumericException extends VariableException {
	private static final long serialVersionUID = 1L;

	public VariableContainsNonNumericException(String name) {
		super(name);
	}
	
	@Override 
	public String getMessage() {
		return "Variable "+name+" has non numeric values";
	}

}
