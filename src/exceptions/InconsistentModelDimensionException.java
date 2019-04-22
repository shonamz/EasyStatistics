package exceptions;

public class InconsistentModelDimensionException extends VariableException {
	private static final long serialVersionUID = 1L;

	public InconsistentModelDimensionException(String name) {
		super(name);
	}

	@Override 
	public String getMessage() {
		return "The the variables in model "+name+" have inconsistent value counts";
	}

	
}
