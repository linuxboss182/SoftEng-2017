package algorithms;

/**
 * Exception for when no path can be found
 */
public class PathNotFoundException
		extends Exception
{
	public PathNotFoundException(String message) {
		super(message);
	}

	public PathNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
