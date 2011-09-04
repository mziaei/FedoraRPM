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
package org.fedoraproject.eclipse.packager.tests.utils.git;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.fedoraproject.eclipse.packager.FedoraProjectRoot;
import org.fedoraproject.eclipse.packager.IProjectRoot;
import org.fedoraproject.eclipse.packager.api.FedoraPackager;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;

public class GitConvertTestCase extends TestCase {

	private static final String PROJECT = "alchemist"; //$NON-NLS-1$
	private static final String SPEC = "alchemist.spec"; //$NON-NLS-1$

	private GitConvertTestProject project;
	private IProject iProject;
	private IProjectRoot fedoraprojectRoot;
	private FedoraPackager packager;

	@Override
	protected void setUp() throws Exception {
		project = new GitConvertTestProject(PROJECT, SPEC);
		iProject = project.getProject();
		// create a fedoraprojectRoot for this project
		fedoraprojectRoot = FedoraPackagerUtils.getProjectRoot((iProject));
		packager = new FedoraPackager(fedoraprojectRoot);
	}

	/**
	 * @return the fedoraprojectRoot
	 */
	public IProjectRoot getFedoraprojectRoot() {
		return fedoraprojectRoot;
	}

	/**
	 * @param fedoraprojectRoot the fedoraprojectRoot to set
	 */
	public void setFedoraprojectRoot(FedoraProjectRoot fedoraprojectRoot) {
		this.fedoraprojectRoot = fedoraprojectRoot;
	}

	@Override
	protected void tearDown() throws Exception {
		project.dispose();
	}

	/**
	 * @return the project
	 */
	public GitConvertTestProject getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(GitConvertTestProject project) {
		this.project = project;
	}

	/**
	 * @return the iProject
	 */
	public IProject getiProject() {
		return iProject;
	}

	/**
	 * @param iProject the iProject to set
	 */
	public void setiProject(IProject iProject) {
		this.iProject = iProject;
	}

	/**
	 * @return FedoraPackager
	 */
	public FedoraPackager getPackager() {
		return packager;
	}

	/**
	 * @param FedoraPackager
	 */
	public void setPackager(FedoraPackager packager) {
		this.packager = packager;
	}
}
