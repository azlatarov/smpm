package custom.utils;

/**
 * This exception is of CHECKED i.e. indicates that caller method should deal with it.
 * @author 	Atanas P. Zlatarov
 *
 */
public class CustomUtilsException extends Exception {
    private static final String padding = "\n\n";
	
	public CustomUtilsException () {}

	public CustomUtilsException (String message) {
		super (padding+message+padding);
    }

	public CustomUtilsException (Throwable cause) {
		super (cause);
    }

	public CustomUtilsException (String message, Throwable cause) {
		super (padding+message+padding, cause);
    }
	
}
