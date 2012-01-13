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
package org.fedoraproject.eclipse.packager.bodhi.internal.ui;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * 
 * Validating username/password by attempting a login on the server.
 *
 */
public class ValidationJob implements IRunnableWithProgress {

	
	private UserValidationResponse response;
	private String username;
	private String password;
	private URL bodhiUrl;
	private String jobName;
	
	/**
	 * 
	 * @param jobName 
	 * @param username
	 * @param password
	 * @param bodhiUrl
	 */
	public ValidationJob(String jobName, String username, String password, URL bodhiUrl) {
		this.jobName = jobName;
		this.username = username;
		this.password = password;
		this.bodhiUrl = bodhiUrl;
	}
	
	@Override
	public void run(IProgressMonitor monitor) {
		monitor.beginTask(this.jobName, IProgressMonitor.UNKNOWN);
		// validate log-in credentials
		response = new UserValidationResponse(username, password, bodhiUrl);
		monitor.done();
	}
	
	/**
	 * 
	 * @return The validation response.
	 */
	public UserValidationResponse getValidationResponse() {
		return this.response;
	}

}
