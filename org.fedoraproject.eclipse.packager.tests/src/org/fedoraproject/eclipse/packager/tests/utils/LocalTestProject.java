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
package org.fedoraproject.eclipse.packager.tests.utils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.fedoraproject.eclipse.packager.LocalProjectType;
import org.fedoraproject.eclipse.packager.api.LocalFedoraPackagerProjectCreator;
import org.osgi.framework.FrameworkUtil;

public class LocalTestProject {
	private static final String PROJECT = "alchemist"; //$NON-NLS-1$
	private static final String SPEC = "alchemist.spec"; //$NON-NLS-1$

	private IProject project;
	private LocalFedoraPackagerProjectCreator mainProject;

	public LocalTestProject(final String packageName) throws CoreException,
			IOException, NoFilepatternException, NoHeadException,
			NoMessageException, ConcurrentRefUpdateException,
			JGitInternalException, WrongRepositoryStateException {
		// Create a base project for the test
		this.project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(PROJECT);
		this.project.create(null);
		this.project.open(null);

		mainProject = new LocalFedoraPackagerProjectCreator(project, null);

		// Find the test SRPM and install it
		URL url = FileLocator.find(FrameworkUtil
				.getBundle(LocalTestProject.class), new Path(
				"resources" + IPath.SEPARATOR + PROJECT + IPath.SEPARATOR + //$NON-NLS-1$
						SPEC), null);
		if (url == null) {
			fail("Unable to find resource" + IPath.SEPARATOR + PROJECT //$NON-NLS-1$
					+ IPath.SEPARATOR + SPEC);
		}

		File externalFile = new File(FileLocator.toFileURL(url).getPath());

		// poulate project using imported .spec file
		mainProject.create(externalFile, LocalProjectType.PLAIN);

		// create the local git repository inside the project
		// add the contents and do the initial commit
		mainProject.createProjectStructure();
	}

	public IProject getProject() {
		return this.project;
	}
	
	public void dispose() throws Exception {
		project.delete(true, true, null);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
}
