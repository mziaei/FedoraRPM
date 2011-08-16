/*******************************************************************************
 * Copyright (c) 2011 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.fedoraproject.eclipse.packager.local.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.linuxtools.rpmstubby.InputType;
import org.fedoraproject.eclipse.packager.local.LocalProjectType;
import org.fedoraproject.eclipse.packager.local.api.LocalFedoraPackagerProjectCreator;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils;
import org.fedoraproject.eclipse.packager.utils.FedoraPackagerUtils.ProjectType;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.FrameworkUtil;

public class LocalFedoraPackgerWizardTest {
	static IWorkspace workspace;
	static IWorkspaceRoot root;
	static NullProgressMonitor monitor;
	private IProject baseProject;
	String pluginRoot;

	final String file_sep = System.getProperty("file.separator"); //$NON-NLS-1$

	@BeforeClass
	public static void setUp() throws Exception {
		IWorkspaceDescription desc;
		workspace = ResourcesPlugin.getWorkspace();
		root = workspace.getRoot();
		monitor = new NullProgressMonitor();
		if (workspace == null) {
			fail("Workspace was not setup");
		}
		if (root == null) {
			fail("Workspace root was not setup");
		}
		desc = workspace.getDescription();
		desc.setAutoBuilding(false);
		workspace.setDescription(desc);
	}

	@Test
	public void testPopulateSrpm() throws Exception {
		// Create a base project for the test 
		baseProject = root.getProject("helloworld");
		baseProject.create(monitor);
		baseProject.open(monitor);

		LocalFedoraPackagerProjectCreator testMainProject = new
				LocalFedoraPackagerProjectCreator(baseProject, monitor);

		// Find the test SRPM and install it
		URL url = FileLocator.find(FrameworkUtil
				.getBundle(LocalFedoraPackgerWizardTest.class), new Path(
				"resources" + file_sep + "helloworld" + file_sep + //$NON-NLS-1$ //$NON-NLS-2$
						"helloworld-2-2.src.rpm"), null);
		if (url == null) {
			fail("Unable to find resource" + file_sep + "helloworld" + file_sep
					+ "helloworld-2-2.src.rpm");
		}
		File externalFile = new File(FileLocator.toFileURL(url).getPath());

		// poulate project using imported SRPM
		testMainProject.create(externalFile, LocalProjectType.SRPM);

		// create the local git repository inside the project
		// add the contents and do the initial commit
//		testMainProject.createProjectStructure();
//
//		// Make sure the original SRPM got copied into the workspace
//		IResource resource = ResourcesPlugin.getWorkspace().getRoot().getProject("helloworld");
//		ProjectType projectType = FedoraPackagerUtils.getProjectType(resource);
//		assertTrue(projectType.equals(ProjectType.GIT));

		// Make sure the original SRPM got copied into the workspace
		IFile srpm = baseProject.getFile(new Path("helloworld-2-2.src.rpm"));
		assertTrue(srpm.exists());

		// Make sure everything got installed properly
		IFile spec = baseProject.getFile(new Path("helloworld.spec"));
		assertTrue(spec.exists());
		IFile sourceBall = baseProject.getFile(new Path("helloworld-2.tar.bz2"));
		assertTrue(sourceBall.exists());
	}

	@Test
	public void testPopulateStubby() throws Exception {
		// Create a base project for test
		baseProject = root.getProject("eclipse-packager");
		baseProject.create(monitor);
		baseProject.open(monitor);

		LocalFedoraPackagerProjectCreator testMainProject = new
				LocalFedoraPackagerProjectCreator(baseProject, monitor);

		// Find the test feature.xml file and install it
		URL url = FileLocator.find(FrameworkUtil
				.getBundle(LocalFedoraPackgerWizardTest.class), new Path(
				"resources" + file_sep + "eclipse-packager" + file_sep + //$NON-NLS-1$ //$NON-NLS-2$
						"feature.xml"), null);
		if (url == null) {
			fail("Unable to find resource" + file_sep + "eclipse-packager" + file_sep
					+ "feature.xml");
		}
		File externalFile = new File(FileLocator.toFileURL(url).getPath());

		// poulate project using imported feature.xml
		testMainProject.create(InputType.ECLIPSE_FEATURE, externalFile);

		// Make sure the original feature.xml got copied into the workspace
		IFile featureFile = baseProject.getFile(new Path("feature.xml"));
		assertTrue(featureFile.exists());

		// Make sure the proper .spec file is generated
		IFile spec = baseProject.getFile(new Path("eclipse-packager.spec"));
		assertTrue(spec.exists());
	}

	@Test
	public void testPopulatePlain() throws Exception {
		// Create a base project for test
		baseProject = root.getProject("helloworld");
		baseProject.create(monitor);
		baseProject.open(monitor);

		LocalFedoraPackagerProjectCreator testMainProject = new
				LocalFedoraPackagerProjectCreator(baseProject, monitor);

		// Find the test .spec file and install it
		URL url = FileLocator.find(FrameworkUtil
				.getBundle(LocalFedoraPackgerWizardTest.class), new Path(
				"resources" + file_sep + "helloworld" + file_sep + //$NON-NLS-1$ //$NON-NLS-2$
						"helloworld.spec"), null);
		if (url == null) {
			fail("Unable to find resource" + file_sep + "helloworld" + file_sep
					+ "helloworld.spec");
		}
		File externalFile = new File(FileLocator.toFileURL(url).getPath());

		// Start a plain project using imported .spec file
		testMainProject.create(externalFile, LocalProjectType.PLAIN);

		// Make sure the original .spec file got copied into the workspace
		IFile featureFile = baseProject.getFile(new Path("helloworld.spec"));
		assertTrue(featureFile.exists());
	}

	@After
	public void tearDown() throws CoreException {
		baseProject.delete(true, false, monitor);
	}
}
