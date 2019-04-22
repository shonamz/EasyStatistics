package exceptions;

public abstract class VariableException extends Exception {
	private static final long serialVersionUID = 1L;
	protected String name;
	
	// main exception class 
	
	VariableException(String name) {
		this.name = name;
	}
	
	public String getVarName() {
		return name;
	};
}
