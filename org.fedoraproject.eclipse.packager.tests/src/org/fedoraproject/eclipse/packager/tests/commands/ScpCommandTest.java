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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.ScpCommand;
import org.fedoraproject.eclipse.packager.api.ScpResult;
import org.fedoraproject.eclipse.packager.tests.utils.LocalSrpmTestProject;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScpCommandTest {

	private static final String PROJECT = "helloworld"; //$NON-NLS-1$
	private static final String SRPM = "helloworld-2-2.src.rpm";
	private static final String SPEC = "helloworld.spec";
	private static final String FAS = "mziaei1"; //$//$NON-NLS-1$

	// project under test
	private LocalSrpmTestProject scpTestProject;
	// main interface class
	private FedoraPackager packager;
	// Fedora packager root
	private IProjectRoot lfpRoot;

	/**
	 * Set up a Fedora project and run the command.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.scpTestProject = new LocalSrpmTestProject(PROJECT, SRPM);
		// create a fedoraprojectRoot for this project
		lfpRoot = FedoraPackagerUtils.getProjectRoot(this.scpTestProject
				.getProject());
		packager = new FedoraPackager(lfpRoot);
	}

	@After
	public void tearDown() throws Exception {
		this.scpTestProject.dispose();
	}

	/**
	 * Test if the command works properly
	 * when there is no remote repository added manually
	 *
	 * @throws Exception
	 */
	@Test
	public void testConvertCommand() throws Exception {

		// Make sure the .src.rpm and .spec files exist in the workspace
		IFile srpm = scpTestProject.getProject().getFile(new Path(SRPM));
		assertTrue(srpm.exists());
		IFile specFile = scpTestProject.getProject().getFile(new Path(SPEC));
		assertTrue(specFile.exists());

		ScpCommand scpCmd;
		scpCmd = (ScpCommand) packager
				.getCommandInstance(ScpCommand.ID);

		ScpResult result = null;
		scpCmd.setSpecFile(SPEC);
		scpCmd.setSrpmFile(SRPM);
		result = scpCmd.setFasAccount(FAS).call(new NullProgressMonitor());

		assertNotNull(result);
		assertTrue(result.wasSuccessful());

	}

}
