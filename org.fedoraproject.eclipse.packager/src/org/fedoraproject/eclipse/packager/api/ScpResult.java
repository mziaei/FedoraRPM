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
package org.fedoraproject.eclipse.packager.api;

/**
 * Represents the result of a {@code ScpCommand}.
 *
 */
public class ScpResult implements ICommandResult {

	private boolean successful = false;
	private String specFile;
	private String srpmFile;

	/**
	 * @param spec
	 * @param srpm
	 */
	public ScpResult(String spec, String srpm) {
		super();
		this.specFile = spec;
		this.srpmFile = srpm;
	}

	/**
	 * @param successful
	 *            the successful to set
	 */
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	/**
	 * See {@link ICommandResult#wasSuccessful()}.
	 */
	@Override
	public boolean wasSuccessful() {
		return successful;
	}

	/**
	 * @param message
	 *            the initial message
	 * @return String the name of the files to scp to be shown to the user
	 */
	public String getHumanReadableMessage(String message) {
		message = message.
				concat("\n*" + specFile + "\n *" + srpmFile); //$NON-NLS-1$ //$NON-NLS-2$

		return message;
	}

}
