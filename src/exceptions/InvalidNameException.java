package exceptions;

public class InvalidNameException extends VariableException {
	private static final long serialVersionUID = 1L;
	private boolean badKeyword;
	private boolean badChar;
	private boolean containsSpace;
	
	public InvalidNameException(String name, boolean badKeyword, 
			boolean badChar, boolean containsSpace) {
		super(name);
		this.badKeyword = badKeyword;
		this.badChar = badChar;
		this.containsSpace = containsSpace;
	}

	@Override
	public String getMessage() {
		if (name == null || name.length()==0) return "Please provide a Variable name";
		if (badKeyword) return name+" is a special keyword";
		if (badChar) return name+ " has a invalid character";
		if (containsSpace) return name+ " cannot contain any spaces";
		return name + " is not a valid Variable name";
	}

}
