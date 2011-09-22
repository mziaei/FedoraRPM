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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jgit.api.Git;
import org.fedoraproject.eclipse.packager.IFpProjectBits;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.api.ScpCommand;
import org.fedoraproject.eclipse.packager.tests.utils.LocalSrpmTestProject;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ScpCommandTest {

	private static final String PROJECT = "helloworld"; //$NON-NLS-1$
	private static final String SRPM = "helloworld-2-2.src.rpm";
	private static final String SPEC = "helloworld.spec";
	private static final String URI = "git://pkgs.fedoraproject.org/eclipse-fedorapackager.git"; //$NON-NLS-1$
	private static final String FAS = "mziaei1"; //$//$NON-NLS-1$

	// project under test
	private LocalSrpmTestProject scpTestProject;
	// main interface class
	private FedoraPackager packager;
	// Fedora packager root
	private IProjectRoot lfpRoot;
	private IFpProjectBits projectBits;
	private Git git;

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
		projectBits = FedoraPackagerUtils.getVcsHandler(lfpRoot);
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

		ScpCommand scpCmd;
		scpCmd = (ScpCommand) packager
				.getCommandInstance(ScpCommand.ID);
		scpCmd.call(new NullProgressMonitor());

	}

}
