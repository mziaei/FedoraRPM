package org.fedoraproject.eclipse.packager.git.api.errors;

import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerAPIException;

/**
 * 
 *
 */
public class LocalProjectConversionFailedException extends
		FedoraPackagerAPIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9098621863061603960L;

	/**
	 * @param message
	 * @param cause
	 */
	public LocalProjectConversionFailedException(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public LocalProjectConversionFailedException(String message) {
		super(message);
	}
}
