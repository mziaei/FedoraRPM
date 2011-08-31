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
package org.fedoraproject.eclipse.packager.tests.commands;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.git.api.ConvertLocalToRemoteCommand;
import org.fedoraproject.eclipse.packager.git.api.errors.LocalProjectConversionFailedException;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.errors.CommandListenerException;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandNotFoundException;
import org.fedoraproject.eclipse.packager.tests.utils.LocalTestProject;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Eclipse plug-in test for ConvertLocalToRemoteCommand.
 */
public class ConvertLocalToRemoteCommandTest {

	// project under test
	private LocalTestProject testProject;
	// main interface class
	private FedoraPackager packager;
	// Local Fedora packager root
	private IProjectRoot lfpRoot;

	/**
	 * Set up a Fedora project and run the command.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testProject = new LocalTestProject("eclipse-mercurial"); //$NON-NLS-1$

		this.lfpRoot = FedoraPackagerUtils.getProjectRoot((this.testProject
				.getProject()));
		this.packager = new FedoraPackager(lfpRoot);
	}

	@After
	public void tearDown() throws Exception {
		this.testProject.dispose();
	}

	/**
	 * Fails if there is not any existing remote repositories for this project
	 * 
	 * @throws FedoraPackagerCommandNotFoundException
	 * @throws FedoraPackagerCommandInitializationException
	 * @throws CommandListenerException
	 * @throws CommandMisconfiguredException
	 * @throws LocalProjectConversionFailedException 
	 * @throws ConvertChangePropertiesFailedException
	 */
	@Test
	public void failNonExistingRemoteRepositories()
			throws FedoraPackagerCommandInitializationException,
			FedoraPackagerCommandNotFoundException,
			CommandMisconfiguredException, CommandListenerException, 
			LocalProjectConversionFailedException {

		ConvertLocalToRemoteCommand convertCmd;
		convertCmd = (ConvertLocalToRemoteCommand) packager
				.getCommandInstance(ConvertLocalToRemoteCommand.ID);
		convertCmd.call(new NullProgressMonitor());
	}

	// @Test
	// public void failAlreadyExistingRemoteRepositories()
	// throws FedoraPackagerCommandInitializationException,
	// FedoraPackagerCommandNotFoundException,
	// CommandMisconfiguredException, CommandListenerException {
	//
	// ConvertLocalToRemoteCommand convertCmd = (ConvertLocalToRemoteCommand)
	// packager
	// .getCommandInstance(ConvertLocalToRemoteCommand.ID);
	// convertCmd.call(new NullProgressMonitor());
	//
	// convertCmd.call(new NullProgressMonitor());
	// }

}
