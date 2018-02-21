package custom.utils;

/**
 * This exception is UNCHECKED i.e. indicates an unexpected situation or bug in the code.
 * @author 	Atanas P. Zlatarov
 *
 */
public class CustomUtilsSevereException extends RuntimeException {
    private static final String padding = "\n\n";
    
	public CustomUtilsSevereException () {}

	public CustomUtilsSevereException (String message) {
		super (padding+message+padding);
    }

	public CustomUtilsSevereException (Throwable cause) {
		super (cause);
    }

	public CustomUtilsSevereException (String message, Throwable cause) {
		super (padding+message+padding, cause);
    }
	
}
