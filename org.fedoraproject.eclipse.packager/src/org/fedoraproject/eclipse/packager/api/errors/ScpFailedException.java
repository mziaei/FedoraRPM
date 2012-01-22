/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.api.errors;

import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerAPIException;

/**
 * Thrown if copying spec and srpm files to the fedorapeople.org
 * was not successful
 *
 */
public class ScpFailedException extends
		FedoraPackagerAPIException {

	private static final long serialVersionUID = 9098621863061603960L;

	/**
	 * @param message
	 */
	public ScpFailedException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ScpFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
