package org.fedoraproject.eclipse.packager.git.errors;

import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerAPIException;

/**
 * 
 *
 */
public class NoLocalGitAvailableException extends FedoraPackagerAPIException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5391982951100028725L;

	/**
	 * @param message
	 * @param cause
	 */
	public NoLocalGitAvailableException(String message,
			Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NoLocalGitAvailableException(String message) {
		super(message);
	}
}
