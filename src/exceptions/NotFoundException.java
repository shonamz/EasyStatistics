package exceptions;

public class NotFoundException extends VariableException {
	private static final long serialVersionUID = 1L;

	public NotFoundException(String name) {
		super(name);
	}
	
	@Override
	public String getMessage() {
		return name + " does not exist";
	}


}
