package exceptions;

public class DuplicateNameException extends VariableException {
	private static final long serialVersionUID = 1L;
	
	public DuplicateNameException(String name) {
		super(name);
	}

	@Override public String getMessage() {
		return name+ " already exists";

	}

}
