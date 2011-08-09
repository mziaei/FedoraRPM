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
package org.fedoraproject.eclipse.packager.bodhi.api;

import org.fedoraproject.eclipse.packager.bodhi.api.errors.BodhiClientException;
import org.fedoraproject.eclipse.packager.bodhi.api.errors.BodhiClientLoginException;

/**
 * Interface for Bodhi clients.
 */
public interface IBodhiClient {

	/**
	 * Bodhi login with username and password.
	 * 
	 * @param username
	 * @param password
	 * @return The parsed response from the server or {@code null}.
	 * @throws BodhiClientLoginException If some error occurred.
	 */
	public BodhiLoginResponse login(String username, String password)
			throws BodhiClientLoginException;
	
	/**
	 * Push a new Bodhi update for one or more builds (i.e. N-V-Rs).
	 * 
	 * @param builds
	 *            N-V-R's for which to push an update for
	 * @param release
	 *            For example "F15".
	 * @param type
	 *            One of "bugfix", "security", "enhancement", "newpackage".
	 * @param request
	 *            {@code testing} or {@code stable}.
	 * @param bugs
	 *            Numbers of bugs to close automatically (comma separated).
	 * @param notes
	 *            The comment for this update.
	 * @param csrf_token
	 * @param suggestReboot
	 *            If a reboot is suggested after this update.
	 * @param enableKarmaAutomatism
	 *            If Karma automatism should be enabled.
	 * @param unpushKarmaThreshold
	 *            The lower unpushing Karma threshold.
	 * @param stableKarmaThreshold
	 *            The upper stable Karma threshold.
	 * @param closeBugsWhenStable
	 *            Flag which determines if bugs should get closed when the
	 *            update hits stable.
	 * @return The update response.
	 * @throws BodhiClientException
	 *             If some error occurred.
	 */
	public BodhiUpdateResponse createNewUpdate(String[] builds, String release,
			String type, String request, String bugs, String notes,
			String csrf_token, boolean suggestReboot,
			boolean enableKarmaAutomatism, int stableKarmaThreshold,
			int unpushKarmaThreshold, boolean closeBugsWhenStable) throws BodhiClientException;

	/**
	 * Log out from Bodhi server.
	 * 
	 * @throws BodhiClientException
	 *             If an error occurred.
	 * 
	 */
	public void logout() throws BodhiClientException;

}