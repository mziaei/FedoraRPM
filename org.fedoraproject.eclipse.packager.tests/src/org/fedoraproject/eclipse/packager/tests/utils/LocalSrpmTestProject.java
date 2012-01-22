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
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.fedoraproject.eclipse.packager.LocalProjectType;
import org.fedoraproject.eclipse.packager.PackagerPlugin;
import org.fedoraproject.eclipse.packager.api.LocalFedoraPackagerProjectCreator;
import org.osgi.framework.FrameworkUtil;

/**
 * Fixture for local projects populated using SRPM
 */
public class LocalSrpmTestProject {
	private IProject project;
	private LocalFedoraPackagerProjectCreator mainProject;

	public LocalSrpmTestProject(final String packageName, final String fileName)
			throws Exception {

		// Create a base project for the test
		this.project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(packageName);
		this.project.create(null);
		this.project.open(null);

		mainProject = new LocalFedoraPackagerProjectCreator(project, null);

		// Find the test external file and install it
		URL url = FileLocator.find(FrameworkUtil
				.getBundle(LocalSrpmTestProject.class), new Path(
				"resources" + IPath.SEPARATOR + packageName + IPath.SEPARATOR + //$NON-NLS-1$
						fileName), null);
		if (url == null) {
			fail("Unable to find resource" + IPath.SEPARATOR + packageName //$NON-NLS-1$
					+ IPath.SEPARATOR + fileName);
		}

		File externalFile = new File(FileLocator.toFileURL(url).getPath());

		// poulate project using imported .srpm file
		mainProject.create(externalFile, LocalProjectType.SRPM);

		// Set persistent property so that we know when to show the context
		// menu item.
		project.setPersistentProperty(PackagerPlugin.PROJECT_LOCAL_PROP, "true" /* unused value */); //$NON-NLS-1$

		ConnectProviderOperation connect = new ConnectProviderOperation(project);
		connect.execute(null);

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void dispose() throws Exception {
		project.delete(true, true, null);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
	}

	/**
	 * Get underlying IProject
	 *
	 * @return
	 */
	public IProject getProject() {
		return this.project;
	}
}
