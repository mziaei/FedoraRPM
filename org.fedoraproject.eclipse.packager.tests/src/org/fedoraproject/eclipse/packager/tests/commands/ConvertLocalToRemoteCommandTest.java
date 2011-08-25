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

import static org.junit.Assert.fail;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.ConvertLocalToRemoteCommand;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.errors.CommandMisconfiguredException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandInitializationException;
import org.fedoraproject.eclipse.packager.api.errors.FedoraPackagerCommandNotFoundException;
import org.fedoraproject.eclipse.packager.api.errors.SourcesUpToDateException;
import org.fedoraproject.eclipse.packager.tests.utils.LocalTestProject;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Eclipse plug-in test for DownloadSourceCommand.
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
	 * Positive results test. Should work fine. Since this is downloading
	 * Eclipse sources it might take a while.
	 * @throws FedoraPackagerCommandNotFoundException 
	 * @throws FedoraPackagerCommandInitializationException 
	 * 
	 * @throws Exception
	 */
	@Test
	public void failNonExistingRemoteRepositories() throws Exception {

		ConvertLocalToRemoteCommand convertCmd = (ConvertLocalToRemoteCommand) packager
				.getCommandInstance(ConvertLocalToRemoteCommand.ID);
		

		try {
			convertCmd.call(new NullProgressMonitor());
		} catch (SourcesUpToDateException e) {
			fail("sources for " + testProject.getProject().getName() + " should not be present");  //$NON-NLS-1$//$NON-NLS-2$
		} catch (CommandMisconfiguredException e) {
			fail("Cmd should be properly configured"); //$NON-NLS-1$
		}
		// pass
	}

}
